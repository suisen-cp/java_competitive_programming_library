# Java 競技プログラミング向けライブラリ

author: <https://atcoder.jp/users/suisen>

`Java` で書かれた競技プログラミング向けのライブラリです．バグは出来るだけ埋め込まないように必ず 1 問以上で verify していますが，それでもバグっている可能性があるのでご了承ください．責任は負いかねます．

また，このライブラリは観賞用の側面が大きいので抽象化を行っているものが多いです．そのため，速度面では特定の用途に特化したコードよりも劣るものも多いことをご了承ください．特に，auto-boxing/unboxing を避けるために primitive type に特化するだけで見違えるほど速くなるものも多いです．(もし余裕が出来れば primitive type 特化のコードも上げていくかもしれません．)

## package `datastructure`

|class|概要|
|-|-|
|[SegmentTree](./datastructure/SegmentTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間でで行うデータ構造．|
|[FenwickTree](./datastructure/FenwickTree.java)|列に対する一点更新および二項演算による区間畳み込みをそれぞれ対数時間でで行うデータ構造．一般的に `SegmentTree` よりも定数倍が軽い．|
