package com.thoughtworks.DDF.Product

import com.thoughtworks.DDF.Arrow.ImpWArrowMin
import com.thoughtworks.DDF.Combinators.Comb
import com.thoughtworks.DDF.Unit.Unit
import com.thoughtworks.DDF.{BEval, ImpW, Loss}

trait ImpWProduct[Info[_], Repr[_]] extends
  Product[Lambda[X => (Info[X], Loss[X])], ImpW[Info, Repr, ?]] with
  ImpWArrowMin[Info, Repr] {
  def base: Product[Info, Repr]

  def baseE: Product[Loss, BEval] = BEvalProduct.apply

  override def rp: Product[Info, Repr] = base

  override def mkProduct[A, B](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B])):
  ImpW[Info, Repr, A => B => (A, B)] =
    ImpW(base.mkProduct(ai._1, bi._1), baseE.mkProduct(ai._2, bi._2))(rcun, becun)

  override def zeroth[A, B](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B])):
  ImpW[Info, Repr, ((A, B)) => A] = ImpW(base.zeroth(ai._1, bi._1), baseE.zeroth(ai._2, bi._2))(rcun, becun)

  override def first[A, B](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B])):
  ImpW[Info, Repr, ((A, B)) => B] = ImpW(base.first(ai._1, bi._1), baseE.first(ai._2, bi._2))(rcun, becun)

  override implicit def productInfo[A, B](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B])):
  (Info[(A, B)], Loss[(A, B)]) = (base.productInfo(ai._1, bi._1), baseE.productInfo(ai._2, bi._2))

  override def productZerothInfo[A, B]: ((Info[(A, B)], Loss[(A, B)])) => (Info[A], Loss[A]) = i =>
    (base.productZerothInfo(i._1), baseE.productZerothInfo(i._2))

  override def productFirstInfo[A, B]: ((Info[(A, B)], Loss[(A, B)])) => (Info[B], Loss[B]) = i =>
    (base.productFirstInfo(i._1), baseE.productFirstInfo(i._2))

  override def curry[A, B, C](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B]), ci: (Info[C], Loss[C])):
  ImpW[Info, Repr, (((A, B)) => C) => A => B => C] =
    ImpW(base.curry(ai._1, bi._1, ci._1), baseE.curry(ai._2, bi._2, ci._2))(rcun, becun)

  override def uncurry[A, B, C](implicit ai: (Info[A], Loss[A]), bi: (Info[B], Loss[B]), ci: (Info[C], Loss[C])):
  ImpW[Info, Repr, (A => B => C) => ((A, B)) => C] =
    ImpW(base.uncurry(ai._1, bi._1, ci._1), baseE.uncurry(ai._2, bi._2, ci._2))(rcun, becun)

}

object ImpWProduct {
  implicit def apply[Info[_], Repr[_]](implicit p: Product[Info, Repr], c: Comb[Info, Repr], u: Unit[Info, Repr]) =
    new ImpWProduct[Info, Repr] {
      override def base: Product[Info, Repr] = p

      override def rcomb: Comb[Info, Repr] = c

      override def runit: Unit[Info, Repr] = u
  }
}
