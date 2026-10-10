# Filtered graph extraction diagnostics

[graphify] MultiDiGraph edge-collapse diagnostic
input: <in-memory>
input_stage: provided JSON (normal graph.json is post-build)
effective_directed: <direct-call>
nodes: 1402
unverified_code_nodes: 0
raw_edges: 2227
valid_candidate_edges: 1836
missing_endpoint_edges: 0
dangling_endpoint_edges: 383
external_reference_edges: 8
self_loop_edges: 7
exact_duplicate_edges: 0
directed_unique_endpoint_pairs: 1785
directed_same_endpoint_collapsed_edges: 51
undirected_unique_endpoint_pairs: 1768
undirected_same_endpoint_collapsed_edges: 68
same_endpoint_group_count: 46
relation_variant_groups: 33
source_file_variant_groups: 0
source_location_variant_groups: 13
context_variant_groups: 1
post_build_graph_type: Graph
post_build_edges: 2149
producer_suppression_sites: 12
producer_suppression_examples:
  - L1646 seen_ids arity=unknown
  - L2179 seen_ids arity=unknown
  - L2181 seen_doc_refs arity=unknown
  - L2551 seen_ids arity=unknown
  - L2698 seen_ids arity=unknown
  - L3424 seen_keys arity=unknown
  - L3593 seen_keys arity=unknown
  - L5669 seen_ids arity=unknown
examples:
  - app_src_main_kotlin_dev_androiddock_prototype_dockeditor -> app_src_main_kotlin_dev_androiddock_prototype_dockeditor_label edges=5 relations=['contains'] locations=['L28', 'L29', 'L30', 'L31', 'L32'] contexts=['']
  - app_src_main_kotlin_dev_androiddock_prototype_clockviewmodel -> app_src_main_kotlin_dev_androiddock_prototype_clockviewmodel_requestlockwallpaper edges=3 relations=['contains', 'declares'] locations=['L46', 'L53'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
  - core_model_src_main_kotlin_dev_androiddock_core_model_chargingstateholder -> core_model_src_main_kotlin_dev_androiddock_core_model_chargingstateholder_chargingenvironment edges=3 relations=['calls', 'contains', 'declares'] locations=['L22', 'L39'] contexts=['', 'Named declaration token in original file; no call relationship implied.', 'call']
  - app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions -> app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions_automaticpermissions edges=2 relations=['contains', 'declares'] locations=['L17'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
  - app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions -> app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions_automaticpermissionpage edges=2 relations=['contains', 'declares'] locations=['L28'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
note: normal graph.json is post-build; raw producer loss must be measured earlier.

# Raw extraction diagnostics

[graphify] MultiDiGraph edge-collapse diagnostic
input: <in-memory>
input_stage: provided JSON (normal graph.json is post-build)
effective_directed: <direct-call>
nodes: 1402
unverified_code_nodes: 0
raw_edges: 2246
valid_candidate_edges: 1855
missing_endpoint_edges: 0
dangling_endpoint_edges: 383
external_reference_edges: 8
self_loop_edges: 26
exact_duplicate_edges: 0
directed_unique_endpoint_pairs: 1804
directed_same_endpoint_collapsed_edges: 51
undirected_unique_endpoint_pairs: 1787
undirected_same_endpoint_collapsed_edges: 68
same_endpoint_group_count: 46
relation_variant_groups: 33
source_file_variant_groups: 0
source_location_variant_groups: 13
context_variant_groups: 1
post_build_graph_type: Graph
post_build_edges: 2168
producer_suppression_sites: 12
producer_suppression_examples:
  - L1646 seen_ids arity=unknown
  - L2179 seen_ids arity=unknown
  - L2181 seen_doc_refs arity=unknown
  - L2551 seen_ids arity=unknown
  - L2698 seen_ids arity=unknown
  - L3424 seen_keys arity=unknown
  - L3593 seen_keys arity=unknown
  - L5669 seen_ids arity=unknown
examples:
  - app_src_main_kotlin_dev_androiddock_prototype_dockeditor -> app_src_main_kotlin_dev_androiddock_prototype_dockeditor_label edges=5 relations=['contains'] locations=['L28', 'L29', 'L30', 'L31', 'L32'] contexts=['']
  - app_src_main_kotlin_dev_androiddock_prototype_clockviewmodel -> app_src_main_kotlin_dev_androiddock_prototype_clockviewmodel_requestlockwallpaper edges=3 relations=['contains', 'declares'] locations=['L46', 'L53'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
  - core_model_src_main_kotlin_dev_androiddock_core_model_chargingstateholder -> core_model_src_main_kotlin_dev_androiddock_core_model_chargingstateholder_chargingenvironment edges=3 relations=['calls', 'contains', 'declares'] locations=['L22', 'L39'] contexts=['', 'Named declaration token in original file; no call relationship implied.', 'call']
  - app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions -> app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions_automaticpermissions edges=2 relations=['contains', 'declares'] locations=['L17'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
  - app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions -> app_src_main_kotlin_dev_androiddock_prototype_automaticpermissions_automaticpermissionpage edges=2 relations=['contains', 'declares'] locations=['L28'] contexts=['', 'Named declaration token in original file; no call relationship implied.']
note: normal graph.json is post-build; raw producer loss must be measured earlier.