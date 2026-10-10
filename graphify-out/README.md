# AndroidDock persistent Graphify

Refreshed 2026-10-10 against the frozen appearance implementation in the working tree above HEAD `ab750b5`. graphifyy 0.9.67 / tree-sitter-kotlin 1.1.0. Final canonical graph: **1576 nodes, 2149 edges, 192 communities**. Compared with the prior 1199/1600/159 snapshot: 427 added and 50 removed node IDs. Removed IDs include replaced document concepts and old line-based ClockViewModel declarations. No product sources were edited by this refresh.

`graph.json`, `graph.html` and `GRAPH_REPORT.md` provide navigation. `DIAGNOSTICS.md` plus filtered/raw JSON retain health evidence. `audit/appearance-refresh.json` records command outcomes, source SHA256, snapshot comparison, manifest queues and exact retained self-loops. `audit/appearance-before/` is the recovery snapshot; `audit/appearance-cli-graph.json` retains the intermediate CLI graph. `audit/appearance-semantic-delta.json` records the seven-document extraction and their current hashes. Earlier `audit/dock-packs-refresh.json` remains historical evidence.

## Commands actually executed

```sh
graphify update .
"$(cat graphify-out/.graphify_python)" graphify-out/build_graph.py --refresh-code
"$(cat graphify-out/.graphify_python)" graphify-out/supplement_kotlin.py
"$(cat graphify-out/.graphify_python)" graphify-out/refresh_appearance_docs.py
"$(cat graphify-out/.graphify_python)" graphify-out/build_graph.py
graphify export html
```

`graphify update .` is AST-only in the installed CLI; it succeeded with 1496 nodes/2089 edges/190 communities. The first helper run stopped at its shrink guard (1460 versus CLI1496). The CLI had added automatic Markdown-heading/output-memory nodes and retained old supplemental line IDs. Its entire graph was retained separately before restoring **only graph.json** from the 1199-node pre-update snapshot and rebuilding from retained AST/semantic/supplement inputs. That recovery succeeded without `--force`. The comparison classifies CLI-only removed nodes by source; the CLI snapshot keeps those records reviewable.

The bounded inventory was extended to the newly partial DockEditor. Seven files now contain 231 original-text declaration tokens, with 200 supplementary nodes. Exact source SHA256, declaration source lines and final graph membership were checked for every inventoried declaration. Anonymous objects, destructuring, full lexical scopes and missing calls remain outside coverage. AST extraction reported no failed sources; parser syntax warnings still affect seven Kotlin sources. This is declaration navigation, not a complete behavior/call graph.

## Targeted semantic coverage

Only PLAN.md, PRODUCT.md, DESIGN.md, .impeccable/design.json, the mockup BRIEF/README and final appearance test report were replaced: 103 document nodes and 96 edges, seven prompt-fingerprinted cache entries. Every other semantic node, edge and hyperedge was preserved exactly, including Dock Packs draft spec/schema/examples. Four unchanged Dock Packs semantic manifest hashes cleared by the raw AST update were restored after current raw-content MD5 verification. Final selected SHA256 and all app/core Kotlin SHA256 were checked against the saved audit.

Seven concept boards and fourteen native capture PNGs are **not semantically indexed**: all 21 lack semantic stamps and remain queued. Brief/README and test-report records provide navigation but do not imply raster analysis. The two overwritten clock-editor captures also remain queued for AST discovery. Prompt-set docs and prior graph-query memory remain pending. The semantic incremental gate also queues 16 changed/new AST-only Kotlin sources because their semantic hashes are empty; AST gate has zero pending code files. Do not interpret that semantic queue as stale AST. The sidecar is explicitly indexed despite hidden-directory discovery exclusion.

## Health and limits

The filtered extraction retains **7 self-loops**, **68 undirected same-endpoint collapse candidates**, **383 dangling import-reference edges**, and zero missing-endpoint records. External nodes are synthesized explicitly for navigation. All raw/full edge records remain in audit/extraction-raw.json and audit/extraction.json; final undirected export can collapse relation/location detail.

19 old self-call guards still match exact edge, source line and full source SHA256; five old guards became invalid after file edits. Guards were byte-preserved. Newly unreviewed self-loops remain visible: ClockViewModel L50, MainActivity L47/L186/L188/L200/L203 and AppearancePresets L42. No new self-call was silently discarded.

Changed communities receive new hub-derived names; unchanged memberships retain curated names. Token usage and monetary cost remain unknown. benchmark.json is explicitly the historical pre-refresh estimate. No browser, Gradle, device or wallpaper checks were run by this graph worker. Native/JVM outcomes are indexed from the final test report, not independently rerun. Graph navigation does not prove runtime behavior, accessibility or image fidelity.
