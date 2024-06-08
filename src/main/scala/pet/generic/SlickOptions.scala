package pet.generic

import net.scalax.simple.codec.{MapGenerc, SimpleFill}
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import slick.jdbc.MySQLProfile.api.*
import slickProfile.*
import slick.ast.ColumnOption

trait SlickOptions[F[_[_]]] {
  import SlickOptions._

  def cv: F[Named] => F[Named]

  def toOpts(p: SimpleProduct.Appender[F]): F[[t] =>> Seq[ColumnOption[t]]] = {
    val simpleFill = SimpleFill[F].derived(p)
    val mapGeneric = MapGenerc[F].derived(p)

    val fill = new SimpleFill.FillI[Named] {
      override def fill[T]: Named[T] = _.seq(List.empty*)
    }
    val default  = simpleFill.fill(fill)
    val newModel = cv(default)

    val mapper = new MapGenerc.MapFunction[Named, [t] =>> Seq[ColumnOption[t]]] {
      def map[X1]: (Appender => Seq[ColumnOption[X1]]) => Seq[ColumnOption[X1]] = inFunc => inFunc(SlickOptions.Appender.value)
    }

    mapGeneric.map(mapper)(newModel)
  }

}

object SlickOptions {

  trait Appender {
    def seq[X1](t: (columnOptions.type => ColumnOption[X1])*): Seq[ColumnOption[X1]] = for ti <- t yield ti(columnOptions)
  }
  object Appender {
    val value: Appender = new Appender {
      //
    }
  }

  type Named[X1] = Appender => Seq[ColumnOption[X1]]

  class DerivedApply[F[_[_]]] {
    def from(m: F[Named] => F[Named]): SlickOptions[F] = new SlickOptions[F] {
      override val cv: F[Named] => F[Named] = m
    }
  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F]

}
