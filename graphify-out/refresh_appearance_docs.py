"""Targeted, host-authored document navigation delta; no raster extraction."""
import json,re,hashlib
from pathlib import Path
from graphify.cache import save_semantic_cache
from graphify.llm import _extraction_system
from graphify.detect import save_manifest
ROOT=Path(__file__).resolve().parent.parent;OUT=ROOT/'graphify-out'
FILES=['PLAN.md','PRODUCT.md','DESIGN.md','.impeccable/design.json','docs/design/mockups/2026-10-10/BRIEF.md','docs/design/mockups/2026-10-10/README.md','docs/evidence/appearance-implementation-test-report.md']
def run():
 old=json.loads((OUT/'audit/semantic.json').read_text()); selected=set(FILES)
 def selected_record(x):
  p=Path(x.get('source_file',''));return str(p.relative_to(ROOT) if p.is_absolute() else p) in selected
 kept={k:[x for x in old.get(k,[]) if not selected_record(x)] for k in ['nodes','edges','hyperedges']}
 nodes=[];edges=[]
 for f in FILES:
  lines=(ROOT/f).read_text().splitlines();prefix='appearance_doc_'+re.sub(r'\W+','_',f).strip('_').lower();root=prefix+'_record'
  def node(nid,label,line,**attrs):nodes.append(dict(id=nid,label=label,file_type='concept',source_file=f,source_location='L'+str(line),**attrs))
  def child(label,line,**attrs):
   nid=prefix+'_l'+str(line);node(nid,label,line,**attrs);edges.append(dict(source=root,target=nid,relation='documents',confidence='EXTRACTED',confidence_score=1.0,weight=1.0,source_file=f,source_location='L'+str(line)));return nid
  node(root,{'PLAN.md':'AndroidDock current roadmap','PRODUCT.md':'AndroidDock current product scope','DESIGN.md':'AndroidDock source-derived design record','.impeccable/design.json':'AndroidDock structured design sidecar'}.get(f,Path(f).name),1)
  if f.endswith('.json'):
   for i,line in enumerate(lines,1):
    m=re.match(r'  "(tokens|components|narrative|meta|version|name|description|examples)"\s*:',line)
    if m:child('Design sidecar '+m[1],i)
  else:
   for i,line in enumerate(lines,1):
    if re.match(r'^#{1,3} ',line):child(line.lstrip('# ').strip(),i)
  concepts={
   'PLAN.md':[('Midnight, Studio, Flip and Gallery implementation priority','Midnight, Studio, Flip и Gallery и поручил'),('Independent photo crops and explicit lock wallpaper confirmation','В объём входят'),('Native POCO evidence replaces historical-only validation','Пользователь подключил POCO')],
   'PRODUCT.md':[('Approved four appearance worlds','Четыре оформления Midnight'),('Current local appearance implementation scope','## Current Scope')],
   'DESIGN.md':[('Native editor and display use distinct palettes','**The Two Palettes Rule'),('Auto size is not verified legibility','**The Fit Is Not Legibility Rule'),('Gallery local photo scrim','**The Local Scrim Rule'),('Source-derived tokens and subsequent native evidence','This record describes')],
   'docs/design/mockups/2026-10-10/BRIEF.md':[('Historical pre-implementation mockup brief','Статус: концепты'),('Lock screen uses photograph without live clock','передаётся изображение')],
   'docs/design/mockups/2026-10-10/README.md':[('Seven concept boards; raster semantics pending','Семь растровых'),('Mockups are not runtime screenshots','Созданы встроенным')],
  }
  for label,needle in concepts.get(f,[]):
   found=next((i for i,l in enumerate(lines,1) if needle in l),None)
   if found:
    nid=prefix+'_concept_'+str(found);node(nid,label,found);edges.append(dict(source=root,target=nid,relation='documents',confidence='EXTRACTED',confidence_score=1.0,weight=1.0,source_file=f,source_location='L'+str(found)))
  for i,line in enumerate(lines,1):
   if f.endswith('test-report.md') and ('passed' in line.lower() or 'не ' in line or 'unverified' in line.lower() or 'not executed' in line.lower() or '59' in line or '14 ' in line):child(line.strip()[:230],i,evidence_status='Recorded test evidence; graph refresh did not execute these tests')
 fresh=dict(nodes=nodes,edges=edges,hyperedges=[],input_tokens=None,output_tokens=None)
 combined={**old,**{k:kept[k]+fresh[k] for k in kept},'input_tokens':None,'output_tokens':None}
 assert all(x in combined[k] for k in kept for x in kept[k])
 (OUT/'audit/semantic.json').write_text(json.dumps(combined,indent=2,ensure_ascii=False))
 cached=save_semantic_cache(nodes,edges,[],root=ROOT,allowed_source_files=[ROOT/f for f in FILES],prompt=_extraction_system())
 save_manifest({'document':[str(ROOT/f) for f in FILES]},root=ROOT,kind='semantic')
 (OUT/'audit/appearance-semantic-delta.json').write_text(json.dumps(dict(selected_files=FILES,source_sha256={f:hashlib.sha256((ROOT/f).read_bytes()).hexdigest() for f in FILES},fresh_fragment=fresh,untouched_counts={k:len(v) for k,v in kept.items()},cache_entries_written=cached,method='Host-authored targeted document concepts and exact headings; no raster analysis',token_usage='unknown'),indent=2,ensure_ascii=False))
 print('Semantic targeted delta',len(nodes),'nodes',len(edges),'edges;',cached,'cache entries')
if __name__=='__main__':run()
