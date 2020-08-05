# Java 競技プログラミング向けライブラリ

author: <https://atcoder.jp/users/suisen>

`Java` で書かれた競技プログラミング向けのライブラリです．バグは出来るだけ埋め込まないように必ず 1 問以上で verify していますが，それでもバグっている可能性があるのでご了承ください．責任は負いかねます．

また，このライブラリは観賞用の側面が大きいので抽象化を行っているものが多いです．そのため，速度面では特定の用途に特化したコードよりも劣るものも多いことをご了承ください．特に，auto-boxing/unboxing を避けるために primitive type に特化するだけで見違えるほど速くなるものも多いです．(もし余裕が出来れば primitive type 特化のコードも上げていくかもしれません．)

## package `collection`

`Java` の Collections Framework を簡易的に再現したクラス群．primitive type 特化などの改造を行うことを想定している．

|class|対応する `java.util` package の class|備考|
|-|-|-|
|[Deque](./collection/Deque.java)|`ArrayDeque`|`ArrayDeque` はランダムアクセスを定数時間で行うことはできないが，`Deque` ではこれが可能．|

## package `datastructure`

データ構造のまとめ．

|class|概要|
|-|-|
|[FenwickTree](./datastructure/FenwickTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．一般的に `SegmentTree` よりも定数倍が軽い．|
|[SegmentTree](./datastructure/SegmentTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．|
|[SparseTable](./datastructure/SparseTable.java)|静的な列に対して，冪等律および結合律を満たす二項演算による区間畳み込みを前計算 $O(N\log N)$，クエリ $O(1)$ で行うデータ構造．前計算を保存するので空間計算量は $O(N\log N)$ である．|
|[UnionFindTree](./datastructure/UnionFindTree.java)|素集合を素集合森を用いて管理するデータ構造．素集合森において，2 つの要素が属する木の merge 操作，ある要素が属する木の根を求めるクエリ処理をそれぞれ「ほぼ」償却定数時間で行うことが出来る．|
