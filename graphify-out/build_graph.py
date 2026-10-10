"""Rebuild reviewed Graphify artifacts from retained AST, semantic and supplement.
No source mutation; host semantic token usage is unknown, never asserted zero.
"""
import json,re,hashlib
from pathlib import Path
from graphify.build import build_from_json
from graphify.cluster import cluster,score_all
from graphify.analyze import god_nodes,surprising_connections,suggest_questions
from graphify.export import to_json
from graphify.report import generate
from graphify.diagnostics import diagnose_extraction,format_diagnostic_report
ROOT=Path(__file__).resolve().parent.parent;OUT=ROOT/'graphify-out'
def read(name):return json.loads((OUT/name).read_text())
def write(name,obj):(OUT/name).write_text(json.dumps(obj,indent=2,ensure_ascii=False))
def filter_reviewed_self_calls(edges,root,guard):
 quarantined=[];kept=[]
 for edge in edges:
  rejected=False
  for approved in guard:
   if edge==approved['edge']:
    path=root/edge['source_file']
    if path.exists() and hashlib.sha256(path.read_bytes()).hexdigest()==approved['source_sha256']:
     line=int(edge['source_location'][1:].split('-')[0]);text=path.read_text().splitlines()[line-1].strip()
     if text==approved['source_line']:
      quarantined.append(approved);rejected=True
    break
  if not rejected:kept.append(edge)
 return kept,quarantined
def run():
 parts=[read('audit/ast.json'),read('audit/semantic.json'),read('.graphify_supplemental.json')]
 nodes=[];edges=[];hyper=[];seen=set()
 for d in parts:
  for n in d['nodes']:
   if n['id'] not in seen:nodes.append(n);seen.add(n['id'])
  edges.extend(d['edges']);hyper.extend(d.get('hyperedges',[]))
 extraction={'nodes':nodes,'edges':edges,'hyperedges':hyper,'input_tokens':None,'output_tokens':None,'token_usage_status':'unknown: collaboration tool provided no usage','supplemental_call_graph_complete':False}
 write('audit/extraction-raw.json',extraction)
 rawdiagnostic=diagnose_extraction(extraction,directed=False,root=str(ROOT))
 write('diagnostics-raw.json',rawdiagnostic)
 kept,quarantined=filter_reviewed_self_calls(edges,ROOT,read('audit/reviewed-self-call-guard.json'))
 (OUT/'audit/quarantined-edges.json').write_text(json.dumps(quarantined,indent=2,ensure_ascii=False))
 extraction['edges']=kept
 extraction['quarantined_self_call_edges']=len(quarantined)
 write('audit/extraction.json',extraction)
 G=build_from_json(extraction,root=str(ROOT),directed=False)
 assert G.number_of_nodes()>0
 communities=cluster(G);cohesion=score_all(G,communities)
 gods=god_nodes(G);surprises=surprising_connections(G,communities)
 labelmap=read('audit/community-names.json')
 labels={}
 for cid,ids in communities.items():
  signature=hashlib.sha256(json.dumps(sorted(ids)).encode()).hexdigest()
  if signature not in labelmap:
   hub=max(ids,key=lambda n:G.degree(n))
   src=G.nodes[hub].get('source_file','')
   stem=Path(src).stem if src else str(G.nodes[hub].get('label',hub))
   words=re.sub(r'(?<=[a-z])(?=[A-Z])',' ',stem).replace('_',' ').replace('-',' ')
   labelmap[signature]=' '.join(words.split()[:4])+' neighborhood'
  labels[cid]=labelmap[signature]
 write('audit/community-names.json',labelmap)
 write('.graphify_labels.json',{str(k):v for k,v in labels.items()})
 questions=suggest_questions(G,communities,labels)
 assert to_json(G,communities,str(OUT/'graph.json'),community_labels=labels),'shrink guard rejected export'
 # Library report expects format-able ints. Replace its compatibility placeholder
 # immediately; persisted extraction and cost metadata have null/unknown values.
 report=generate(G,communities,cohesion,labels,gods,surprises,read('audit/detection.json'),{'input':0,'output':0},str(ROOT),suggested_questions=questions)
 report=re.sub(r'^- Token cost:.*$', '- Token cost: unknown (host collaboration tool did not expose input/output usage; no monetary cost estimate).',report,flags=re.M)
 report+='\n## Coverage and integrity limitations\n\nSeven Kotlin files have partial AST from tree-sitter-kotlin 1.1.0. Original-text supplemental declarations add file/declaration edges only, never missing calls. See KOTLIN_COVERAGE.md and .graphify_supplemental.json. 20 evidence JSON files were processed but yielded no structural nodes. Full call-graph coverage is not claimed.\n'
 diagnostic=diagnose_extraction(extraction,directed=False,root=str(ROOT))
 write('diagnostics.json',diagnostic)
 diagtext=format_diagnostic_report(diagnostic)
 (OUT/'DIAGNOSTICS.md').write_text('# Filtered graph extraction diagnostics\n\n'+diagtext+'\n\n# Raw extraction diagnostics\n\n'+format_diagnostic_report(rawdiagnostic))
 flags=[f'{diagnostic[k]} {k}' for k in ('dangling_endpoint_edges','missing_endpoint_edges','self_loop_edges','undirected_same_endpoint_collapsed_edges') if diagnostic.get(k,0)]
 if flags:report+='\nGraph health warning: '+ '; '.join(flags)+'. Full original edge records remain in audit/extraction.json.\n'
 report+='\nRaw health: '+str(rawdiagnostic['dangling_endpoint_edges'])+' import edges reference '+str(len({e.get('target') for e in extraction['edges'] if e.get('target') not in {n['id'] for n in extraction['nodes']}}))+' external endpoint IDs. '+str(len(quarantined))+' self-call resolver artifacts were quarantined in audit/quarantined-edges.json. Filtered health: '+str(diagnostic['self_loop_edges'])+' self loops. Synthesized import nodes have external=true/type=external; they are references, not extracted declarations. Same-endpoint edge collapse can lose relation/location detail; retain audit/extraction-raw.json and audit/extraction.json for full records.\n'
 (OUT/'GRAPH_REPORT.md').write_text(report)
 write('.graphify_analysis.json',{'communities':{str(k):v for k,v in communities.items()},'cohesion':{str(k):v for k,v in cohesion.items()},'gods':gods,'surprises':surprises,'questions':questions})
 print('Graph:',G.number_of_nodes(),'nodes;',G.number_of_edges(),'edges;',len(communities),'communities')
 print(diagtext)
 print('COMMUNITY LABEL INPUT')
 for cid,ids in communities.items():
  print(cid,len(ids),':',', '.join(str(G.nodes[n].get('label',n)) for n in ids[:16]))
if __name__=='__main__':
 import sys
 if '--refresh-code' in sys.argv:
  from graphify.detect import detect
  from graphify.extract import extract
  detection=detect(ROOT);write('audit/detection.json',detection)
  result=extract([Path(f) for f in detection['files'].get('code',[])],cache_root=ROOT,parallel=False)
  write('audit/ast.json',result)
  from supplement_kotlin import run as supplement
  supplement()
 run()
