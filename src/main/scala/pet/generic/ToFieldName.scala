package pet.generic

import net.scalax.simple.codec.MapGenerc
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import sttp.tapir.FieldName

trait ToFieldName[F[_[_]]] {
  def model: F[[_] =>> FieldName]
}

object ToFieldName {

  class DerivedApply[F[_[_]]] {

    def derived(
      p: SimpleProduct.Appender[F],
      nameModel: F[[_] =>> String]
    ): ToFieldName[F] = {
      val mapGeneric = MapGenerc[F].derived(p)

      val mapper = new MapGenerc.MapFunction[[t] =>> String, [t] =>> FieldName] {
        def map[X1]: String => FieldName = inFunc => FieldName(inFunc)
      }

      val m = mapGeneric.map(mapper)(nameModel)

      new ToFieldName[F] {
        override def model: F[[_] =>> FieldName] = m
      }
    }

  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F] {
    //
  }

}
