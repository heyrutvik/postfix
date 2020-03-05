package io.github.heyrutvik.postfix

sealed trait TopLevelExpr

sealed trait Program extends TopLevelExpr
object Program {
  final case class Prog(args: Stack, seq: CmdSeq) extends Program
}

sealed trait CmdSeq extends TopLevelExpr
object CmdSeq {
  final case class Seq(cs: List[Cmd]) extends CmdSeq
}

sealed trait Cmd
object Cmd {
  final case class IntLit(num: Int) extends Cmd
  final case object Pop extends Cmd
  final case object Swap extends Cmd
  final case object Nget extends Cmd
  final case object Sel extends Cmd
  final case object Exec extends Cmd
  final case class A(op: ArithOp) extends Cmd
  final case class R(op: RelOp) extends Cmd
  final case class Q(cs: CmdSeq) extends Cmd
}

sealed trait ArithOp
object ArithOp {
  final case object Add extends ArithOp
  final case object Sub extends ArithOp
  final case object Mul extends ArithOp
  final case object Div extends ArithOp
  final case object Rem extends ArithOp
}

sealed trait RelOp
object RelOp {
  final case object Lt extends RelOp
  final case object Eq extends RelOp
  final case object Gt extends RelOp
}

sealed trait Stack extends TopLevelExpr
object Stack {
  final case class St(vs: List[Value]) extends Stack
}