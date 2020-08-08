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
|[DualSegmentTree](./datastructure/DualSegmentTree.java)|列に対する区間作用および一点取得をそれぞれ対数時間で行うデータ構造．遅延セグメント木よりも機能は制限されているが，その分定数倍が高速．|
|[FenwickTree](./datastructure/FenwickTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．一般的に `SegmentTree` よりも定数倍が軽い．|
|[LazySegmentTree](./datastructure/LazySegmentTree.java)|列に対する区間作用および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．機能的には `SegmentTree` の上位互換であるが，定数倍が結構重い．一点更新は未実装．|
|[SegmentTree](./datastructure/SegmentTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間で行うデータ構造．|
|[SparseTable](./datastructure/SparseTable.java)|サイズ N の静的な列に対して，冪等律および結合律を満たす二項演算による区間畳み込みを前計算 &Theta;(NlogN)，クエリ &Theta;(1) で行うデータ構造．前計算を保存するので空間計算量は &Theta;(NlogN)．|
|[UnionFindTree](./datastructure/UnionFindTree.java)|素集合を素集合森を用いて管理するデータ構造．素集合森において，2 つの要素が属する木の merge 操作，ある要素が属する木の根を求めるクエリ処理をそれぞれ「ほぼ」償却定数時間で行うことが出来る．|

## package `integer`

整数論関連のパッケージ．

|class|概要|
|-|-|
|[DiscreteLogarithm](./integer/DiscreteLogarithm.java)|離散対数問題を解くクラス．剰余の法を P として計算量は &Theta;(&Sqrt;P)．|
|[ModArithmetic](./integer/ModArithmetic.java)|剰余演算関連をまとめたクラス．乗法逆元の計算や二分累乗法などの実装を含む．|

## package `ints`

主に `datastructure` package や `collection` package を `int` 型特化に書き換えたものです．boxing が走らないのでかなり定数倍が高速になり，メモリ使用量も削減されます．

中身はジェネリクスを用いて書かれたものと同じなので説明は省略します．

## package `longs`

主に `datastructure` package や `collection` package を `long` 型特化に書き換えたものです．boxing が走らないのでかなり定数倍が高速になり，メモリ使用量も削減されます．

中身はジェネリクスを用いて書かれたものと同じなので説明は省略します．
