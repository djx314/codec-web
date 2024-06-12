package pet.generic

import net.scalax.simple.codec.{IndexModel, MapGenerc}
import net.scalax.simple.codec.to_list_generic.SimpleProduct

trait GetFieldModel[F[_[_]]] {
  def getFieldModel[I[_]]: F[[t] =>> F[I] => I[t]]
}

object GetFieldModel {

  class DerivedApply[F[_[_]] <: Product] {

    def derived(
      p: SimpleProduct.Appender[F],
      indexModel: IndexModel[F]
    ): GetFieldModel[F] = {
      val mapGeneric = MapGenerc[F].derived(p)

      new GetFieldModel[F] {
        override def getFieldModel[I[_]]: F[[t] =>> F[I] => I[t]] = {
          val mapper = new MapGenerc.MapFunction[[t] =>> Int, [t] =>> F[I] => I[t]] {
            def map[X1]: Int => F[I] => I[X1] = n => model => model.productElement(n).asInstanceOf[I[X1]]
          }

          mapGeneric.map(mapper)(indexModel.model)
        }
      }
    }

  }

  def apply[F[_[_]] <: Product]: DerivedApply[F] = new DerivedApply[F] {
    //
  }

}
