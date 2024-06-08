package pet.generic

import net.scalax.simple.codec.{MapGenerc, SimpleFill, ZipGeneric}
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import slick.jdbc.MySQLProfile.api.*
import slickProfile.*
import slick.ast.{ColumnOption, TypedType}

trait SlickTableRep[F[_[_]]] {
  def table(t: Table[?]): F[Rep]
}

object SlickTableRep {

  case class Describe(describe: String) extends ColumnOption[Nothing]

  class DerivedApply[F[_[_]]] {
    def derivedImpl(
      p: SimpleProduct.Appender[F],
      named: SlickNamed[F],
      describe: SlickDescribe[F],
      opt: SlickOptions[F],
      typedType: F[TypedType]
    ): Table[?] => F[Rep] = { (table: Table[?]) =>
      val simpleFill = SimpleFill[F].derived(p)
      val mapGeneric = MapGenerc[F].derived(p)
      val zipGeneric = ZipGeneric[F].derived(p)

      val zipModel = zipGeneric.zip(describe.labelled, opt.toOpts(p))

      val mapper = new MapGenerc.MapFunction[[t] =>> (String, Seq[ColumnOption[t]]), [t] =>> Seq[ColumnOption[t]]] {
        def map[X1] = (inFunc: (String, Seq[ColumnOption[X1]])) => SlickTableRep.Describe(inFunc._1) +: inFunc._2
      }

      val model2: F[[t] =>> Seq[ColumnOption[t]]] = mapGeneric.map(mapper)(zipModel)

      val zip2Model = zipGeneric.zip(zipGeneric.zip(named.labelled, opt.toOpts(p)), typedType)

      val mapper2 = new MapGenerc.MapFunction[[t] =>> ((String, Seq[ColumnOption[t]]), TypedType[t]), Rep] {
        def map[X1] = (inFunc: ((String, Seq[ColumnOption[X1]]), TypedType[X1])) => table.column(inFunc._1._1, inFunc._1._2*)(inFunc._2)
      }

      mapGeneric.map(mapper2)(zip2Model)
    }

    def derived(
      p: SimpleProduct.Appender[F],
      named: SlickNamed[F],
      describe: SlickDescribe[F],
      opt: SlickOptions[F],
      typedType: F[TypedType]
    ): SlickTableRep[F] = new SlickTableRep[F] {
      def table(t: Table[?]): F[Rep] = derivedImpl(p, named, describe, opt, typedType)(t)
    }

  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F] {
    //
  }

}
