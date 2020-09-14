# Java 競技プログラミング向けライブラリ

author: <https://atcoder.jp/users/suisen>

`Java` で書かれた競技プログラミング向けのライブラリです．バグは出来るだけ埋め込まないように必ず 1 問以上で verify していますが，それでもバグっている可能性があるのでご了承ください．責任は負いかねます．

また，このライブラリは観賞用の側面が大きいので抽象化を行っているものが多いです．そのため，速度面では特定の用途に特化したコードよりも劣るものも多いことをご了承ください．特に，auto-boxing/unboxing を避けるために primitive type に特化するだけで見違えるほど速くなるものも多いです．primitive type 特化のコードも順次上げていきます．

## package `collection`

`Java` の Collections Framework を簡易的に再現したクラス群．primitive type 特化などの改造を行うことを想定している．

|class|対応する `java.util` package の class|備考|
|-|-|-|
|[Deque](./collection/Deque.java)|`ArrayDeque`|`ArrayDeque` はランダムアクセスを定数時間で行うことはできないが，`Deque` ではこれが可能．|
|[PriorityQueue](./collection/PriorityQueue.java)|`PriorityQueue`|標準ライブラリよりも僅かに遅いが，問題ない程度．`int` や `long` 特化に書き換えて初めて真価を発揮する．|

## package `datastructure`

データ構造のまとめ．

|class|概要|
|-|-|
|[ConvexHullTrick](./datastructure/ConvexHullTrick.java)|直線の集合 L に対して，直線 ax+b を L に追加する操作と，座標 x に対して L の中で最小値 (/最大値) を取る直線の値を求める操作の2種類のクエリを処理することができる．本ライブラリの実装では，N 本の直線追加と Q 回の最小値 (/最大値) クエリを O(NlogN+Q(logN)^2) で行うことが出来る．|
|[DualSegmentTree](./datastructure/DualSegmentTree.java)|列に対する区間作用および一点取得をそれぞれ対数時間で行うデータ構造．遅延セグメント木よりも機能は制限されているが，その分定数倍が高速．|
|[FenwickTree](./datastructure/FenwickTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．一般的に `SegmentTree` よりも定数倍が軽い．|
|[LazySegmentTree](./datastructure/LazySegmentTree.java)|列に対する区間作用および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．機能的には `SegmentTree` の上位互換であるが，定数倍が結構重い．一点更新は未実装．|
|[MonotoneConvexHullTrick](./datastructure/MonotoneConvexHullTrick.java)|`ConvexHullTrick` において，追加する直線の傾きが広義単調増加 (/広義単調減少) である場合にはクエリの処理を高速化することが出来る．本ライブラリの実装では，N 本の (単調に傾きの値が変化する) 直線の追加と Q 回の最小値 (/最大値) クエリを O(N+QlogN) で行うことが出来る．|
|[SegmentTree](./datastructure/SegmentTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．|
|[SparseTable](./datastructure/SparseTable.java)|サイズ N の静的な列に対して，冪等律および結合律を満たす二項演算による区間畳み込みを前計算 &Theta;(NlogN)，クエリ &Theta;(1) で行うデータ構造．前計算を保存するので空間計算量は &Theta;(NlogN)．|
|[UnionFindTree](./datastructure/UnionFindTree.java)|素集合を素集合森を用いて管理するデータ構造．素集合森において，2 つの要素が属する木の merge 操作，ある要素が属する木の根を求めるクエリ処理をそれぞれ「ほぼ」償却定数時間で行うことが出来る．|

## package `graph`

木を除くグラフに関するパッケージ．

|class|概要|
|-|-|
|[BCC](./graph/BCC.java)|二重辺連結成分分解を行う．頂点数を N，辺数を M として 計算量は &Theta;(N+M)．|
|[BellmanFord](./graph/BellmanFord.java)|ベルマンフォード法により単一始点最短経路問題 (SSSP) を解く．辺の重みが負であってもよい．頂点数を N，辺数を M として 計算量は &Theta;(MN)．|
|[Dijkstra](./graph/Dijkstra.java)|ダイクストラ法により単一始点最短経路問題 (SSSP) を解く．但し，辺の重みが負であってはならない．頂点数を N，辺数を M として 計算量は &Theta;((M+N)logN)．|
|[Graph](./graph/Graph.java)|グラフを表現するための基本的なクラスのまとめ．|
|[MaxFlow](./graph/MaxFlow.java)|最大流問題を解きます．Ford Fulkerson のアルゴリズムと Dinic のアルゴリズムが実装されています．<br>Ford Fulkerson の計算量は，流量を F，辺数を E として &Theta;(FE) です．<br>Dinic の計算量は，頂点数を V，辺数を E とすると，一般の場合では O(EV^2) です．ただし，多くのケースでは高速に動作します．また，二部グラフや容量一定のグラフではオーダーレベルで計算量が改善されます．|
|[MinCostFlow](./graph/MinCostFlow.java)|最小費用流問題を Primal-Dual 法により解きます．本実装では負辺に対応していません．計算量は，流量 F，頂点数 V，辺数 E として &Theta;(F(V+E)logV) です．|
|[Kruskal](./graph/Kruskal.java)|クラスカル法により最小全域木 (MST) を構築する．頂点数を N，辺数を M として 計算量は &Theta;(MlogN)．|
|[Prim](./graph/Prim.java)|プリム法により最小全域木 (MST) を構築する．頂点数を N，辺数を M として 計算量は &Theta;(MlogN)．|
|[SCC](./graph/SCC.java)|強連結成分分解を行う．頂点数を N，辺数を M として 計算量は &Theta;(N+M)．|

## package `integer`

整数論関連のパッケージ．

|class|概要|
|-|-|
|[DiscreteLogarithm](./integer/DiscreteLogarithm.java)|離散対数問題を解くクラス．剰余の法を P として計算量は &Theta;(&Sqrt;P)．|
|[ModArithmetic](./integer/ModArithmetic.java)|剰余演算関連をまとめたクラス．乗法逆元の計算や二分累乗法などの実装を含む．|

## package `util`

`Pair` 等のユーティリティクラスのパッケージ．アルゴリズム / データ構造の実装の際に補助的に用いる程度．

## package `ints`

主に `datastructure` package や `collection` package を `int` 型特化に書き換えたクラス群．boxing が走らないのでかなり定数倍が高速になり，メモリ使用量の削減も期待される．

ジェネリクスを用いて書かれたものと同じクラスの説明は省略．

## package `longs`

主に `datastructure` package や `collection` package を `long` 型特化に書き換えたクラス群．boxing が走らないのでかなり定数倍が高速になり，メモリ使用量の削減も期待される．

ジェネリクスを用いて書かれたものと同じクラスの説明は省略．

|class|概要|
|-|-|
|[LongOrderedMap](./longs/datastructure/LongOrderedMap.java)|Randomized Binary Search Tree (RBST) による平衡二分探索木の実装．標準ライブラリの `java.util.TreeMap` ではサポートされていない，k 番目の要素取得や木の merge / split 操作をサポートしている．|
