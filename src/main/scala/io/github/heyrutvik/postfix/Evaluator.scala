package io.github.heyrutvik.postfix

import io.github.heyrutvik.postfix.Printer._
import io.github.heyrutvik.postfix.PrinterInstances._
import scala.io.AnsiColor._

import scala.annotation.tailrec

object Evaluator {
  final case class Config(seq: CmdSeq, st: Stack)

  private def nget(i: Int, s: List[Value]): List[Value] = s(i) match {
    case v @ Value.IntLit(_) => v :: s
    case Value.CommandSeq(cs) =>
      throw new RuntimeException(s"nget: ${print[CmdSeq](CmdSeq.Seq(cs))} found in stack at the index $i.")
  }

  private def calc(op: ArithOp, a: Int, b: Int): Int = op match {
    case ArithOp.Add => a + b
    case ArithOp.Sub => a - b
    case ArithOp.Mul => a * b
    case ArithOp.Div => a / b
    case ArithOp.Rem => a % b
  }

  private def calc(op: RelOp, a: Int, b: Int): Int = op match {
    case RelOp.Lt => if (a < b) 1 else 0
    case RelOp.Eq => if (a == b) 1 else 0
    case RelOp.Gt => if (a > b) 1 else 0
  }

  private def ==> : Config => Config = {
    case Config(CmdSeq.Seq(Cmd.IntLit(n) :: cs), Stack.St(vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(Value.IntLit(n) :: vs)) // num
    case Config(CmdSeq.Seq(Cmd.Q(CmdSeq.Seq(cs0)) :: cs), Stack.St(vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(Value.CommandSeq(cs0) :: vs)) // seq
    case Config(CmdSeq.Seq(Cmd.Pop :: cs), Stack.St(_ :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(vs)) // pop
    case Config(CmdSeq.Seq(Cmd.Nget :: cs), Stack.St(Value.IntLit(i) :: vs)) if i > 0 && i < vs.length =>
      Config(CmdSeq.Seq(cs), Stack.St(nget(i, vs))) // nget
    case Config(CmdSeq.Seq(Cmd.Swap :: cs), Stack.St(v1 :: v2 :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(v2 :: v1 :: vs)) // swap
    case Config(CmdSeq.Seq(Cmd.Sel :: cs), Stack.St(vfalse :: _ :: Value.IntLit(0) :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(vfalse :: vs)) // sel-false
    case Config(CmdSeq.Seq(Cmd.Sel :: cs), Stack.St(_ :: vtrue :: _ :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(vtrue :: vs)) // sel-true
    case Config(CmdSeq.Seq(Cmd.Exec :: cs), Stack.St(Value.CommandSeq(exec) :: vs)) =>
      Config(CmdSeq.Seq(exec ::: cs), Stack.St(vs)) // exec
    case Config(CmdSeq.Seq(Cmd.A(op) :: cs), Stack.St(Value.IntLit(v1) :: Value.IntLit(v2) :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(Value.IntLit(calc(op, v2, v1)) :: vs)) // arithop
    case Config(CmdSeq.Seq(Cmd.R(op) :: cs), Stack.St(Value.IntLit(v1) :: Value.IntLit(v2) :: vs)) =>
      Config(CmdSeq.Seq(cs), Stack.St(Value.IntLit(calc(op, v2, v1)) :: vs)) // relop
    case Config(seq, st) =>
      throw new RuntimeException(s"eval error: command seq '${print(seq)}' can not be applied to the stack '${print(st)}'.")
  }

  private def output(cf: Config): Int = cf match {
    case Config(CmdSeq.Seq(Nil), Stack.St(Value.IntLit(n) :: _)) => n
    case Config(CmdSeq.Seq(Nil), Stack.St(Value.CommandSeq(cs) :: _)) => throw new RuntimeException(s"output error: ${print[CmdSeq](CmdSeq.Seq(cs))} was at the top of the stack!")
    case Config(CmdSeq.Seq(Nil), _) => throw new RuntimeException("output error [stuck state]: stack is empty!")
    case _ => throw new RuntimeException("output error [stuck state]: something is wrong!")
  }


  def input(p: Program): Config = p match {
    case Program.Prog(stack, seq) => Config(seq, stack)
  }

  @tailrec
  def eval(cf: Config, debug: Boolean): Int = cf match {
    case Config(CmdSeq.Seq(Nil), _) =>
      if (debug) println(s"${RED}${print(cf.seq)}${RESET} ${GREEN}${print(cf.st)}${RESET}")
      output(cf)
    case _ =>
      val cfp = ==>(cf)
      if (debug) println(s"${RED}${print(cf.seq)}${RESET} ${GREEN}${print(cf.st)}${RESET}")
      eval(cfp, debug)
  }
}

sealed trait Value
object Value {
  final case class IntLit(n: Int) extends Value
  final case class CommandSeq(cs: List[Cmd]) extends Value
}