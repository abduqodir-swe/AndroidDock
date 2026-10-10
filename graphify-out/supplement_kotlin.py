"""Bounded Kotlin declaration inventory; never infers calls or semantic containment.
Reads original text, masks strings/comments, keeps exact line numbers. Owns only
supplement artifacts. AST identities are reused for already recovered declarations.
"""
import json,re,hashlib
from pathlib import Path
ROOT=Path(__file__).resolve().parent.parent
OUT=ROOT/'graphify-out'
FILES=['app/src/main/kotlin/dev/androiddock/prototype/'+f+'.kt' for f in ['AutomaticPermissions','AutomaticRestoreReceiver','AutomaticSettingsActivity','ChargingWatchService','ClockViewModel','DockEditor']]+['core/model/src/main/kotlin/dev/androiddock/core/model/ChargingStateHolder.kt']
def mask(s):
 out=list(s);i=0;state='code';depth=0
 while i<len(s):
  start=i
  if state=='code':
   if s.startswith('//',i):state='line';i+=2
   elif s.startswith('/*',i):state='block';depth=1;i+=2
   elif s.startswith('"""',i):state='triple';i+=3
   elif s[i] in ('"',"'"):state=s[i];i+=1
   else:i+=1;continue
  elif state=='line':
   if s[i]=='\n':state='code'
   i+=1
  elif state=='block':
   if s.startswith('/*',i):depth+=1;i+=2
   elif s.startswith('*/',i):depth-=1;i+=2;state='code' if depth==0 else state
   else:i+=1
  elif state=='triple':
   if s.startswith('"""',i):state='code';i+=3
   else:i+=1
  elif state in ('"',"'"):
   if s[i]=='\\':i+=2
   elif s[i]==state:state='code';i+=1
   else:i+=1
  for j in range(start,min(i,len(s))):
   if s[j]!='\n':out[j]=' '
 return ''.join(out)
PAT=re.compile(r'\b(class|object|fun|val|var)\s+([A-Za-z_]\w*(?:\.[A-Za-z_]\w*)*)|\b(companion)\s+object\b')
def run():
 ast=json.loads((OUT/'audit/ast.json').read_text())
 nodes=[];edges=[];audit=[]
 for f in FILES:
  s=(ROOT/f).read_text();clean=mask(s)
  fn=next(n for n in ast['nodes'] if n.get('source_file')==f and n.get('label')==Path(f).name)
  inventory=[]
  for m in PAT.finditer(clean):
   kind=m.group(1) or 'object';name=m.group(2) or 'Companion';line=s.count('\n',0,m.start())+1
   # `object : Base` is anonymous; PAT deliberately does not guess a name.
   label=name+'()' if kind=='fun' else name
   candidates=[n for n in ast['nodes'] if n.get('source_file')==f and n.get('label')==label and abs(int(n.get('source_location','L0').split('-')[0][1:])-line)<=2]
   if candidates:
    nid=candidates[0]['id'];added=False
   else:
    nid=fn['id']+'_supp_'+kind+'_'+name.replace('.','_').lower()+'_l'+str(line)
    node={'id':nid,'label':label+' ['+kind+' L'+str(line)+']','declaration_name':name,'file_type':'code','source_file':f,'source_location':'L'+str(line),'_origin':'supplemental_declaration','declaration_kind':kind,'extraction_method':'lexical_declaration_original_text','description':'Original-text declaration only; lexical scope and call targets are not inferred.'}
    if kind in ('fun','class'):node['_callable']=True
    if kind=='class':node['_callable_class']=True
    nodes.append(node);added=True
   # File containment is literal, not an inferred class/call relationship.
   edges.append({'source':fn['id'],'target':nid,'relation':'declares','confidence':'EXTRACTED','weight':1.0,'source_file':f,'source_location':'L'+str(line),'_origin':'supplemental_declaration','context':'Named declaration token in original file; no call relationship implied.'})
   inventory.append({'kind':kind,'name':name,'line':line,'node_id':nid,'added':added,'source_line':s.splitlines()[line-1].strip()})
  audit.append({'source_file':f,'sha256':hashlib.sha256(s.encode()).hexdigest(),'declarations':inventory,'call_graph_complete':False})
 supplement={'nodes':nodes,'edges':edges,'hyperedges':[],'input_tokens':0,'output_tokens':0,'method':'bounded original-text declaration inventory; no reconstructed calls','files':audit}
 (OUT/'.graphify_supplemental.json').write_text(json.dumps(supplement,indent=2,ensure_ascii=False))
 lines=['# Kotlin declaration coverage audit','','tree-sitter-kotlin 1.1.0 produced partial AST for these seven Kotlin sources. Supplemental inventory reads current original text, masks comments/strings, and records named class/object/function/property/local/constructor binding tokens with exact original lines. Only file → declaration edges are added. Anonymous objects, destructuring and full lexical scopes are outside this limited extractor. No missing calls are reconstructed. Source SHA256 and declaration inventory are retained in `.graphify_supplemental.json`.','','| File | Declaration tokens | Added nodes | Functions |','| --- | ---: | ---: | --- |']
 for a in audit:
  inv=a['declarations'];func=[x['name']+' L'+str(x['line']) for x in inv if x['kind']=='fun']
  lines.append('| '+a['source_file']+' | '+str(len(inv))+' | '+str(sum(x['added'] for x in inv))+' | '+', '.join(func)+' |')
 lines+=['','All seven call graphs remain incomplete. AutomaticRestoreReceiver and ChargingWatchService originally contained only their file node; their named declarations are now inventoried, but no AST-equivalent behavior guarantee is claimed.','']
 (OUT/'KOTLIN_COVERAGE.md').write_text('\n'.join(lines))
 print('Supplement:',len(nodes),'new nodes,',len(edges),'declaration edges')
 for a in audit:print(a['source_file'],len(a['declarations']),'declaration tokens')
if __name__=='__main__':run()
