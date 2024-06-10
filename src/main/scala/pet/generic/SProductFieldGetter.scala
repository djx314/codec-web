package pet.generic

import net.scalax.simple.codec.{MapGenerc, ZipGeneric}
import net.scalax.simple.codec.to_list_generic.{SimpleProduct, ToListByTheSameTypeGeneric}
import sttp.tapir.{FieldName, Schema}
import sttp.tapir.SchemaType.{SProduct, SProductField}
import cats.Id
import sttp.tapir.Schema.SName

trait SProductFieldGetter[F[_[_]]] {
  def model: F[[_] =>> SProductField[F[Id]]]
  def objectSchema(modelName: SName): Schema[F[Id]]
}

object SProductFieldGetter {

  class DerivedApply[F[_[_]]] {

    def derived(
      p: SimpleProduct.Appender[F],
      fieldName: ToFieldName[F],
      schema: F[Schema],
      getFieldModel: GetFieldModel[F]
    ): SProductFieldGetter[F] = {
      val mapGeneric    = MapGenerc[F].derived(p)
      val zipGeneric    = ZipGeneric[F].derived(p)
      val toListGeneric = ToListByTheSameTypeGeneric[F].derived(p)

      val zipModel = zipGeneric.zip(zipGeneric.zip(fieldName.model, schema), getFieldModel.getFieldModel[Id])

      val mapper = new MapGenerc.MapFunction[[t] =>> ((FieldName, Schema[t]), F[Id] => t), [t] =>> SProductField[F[Id]]] {
        def map[X1]: (((FieldName, Schema[X1]), F[Id] => X1)) => SProductField[F[Id]] = (t: ((FieldName, Schema[X1]), F[Id] => X1)) =>
          SProductField[F[Id], X1](t._1._1, t._1._2, t._2.andThen(Option.apply))
      }

      val m                                = mapGeneric.map(mapper)(zipModel)
      val list: List[SProductField[F[Id]]] = toListGeneric.toListByTheSameType[SProductField[F[Id]]](m)

      new SProductFieldGetter[F] {
        override def model: F[[_] =>> SProductField[F[Id]]]    = m
        override def objectSchema(sName: SName): Schema[F[Id]] = Schema(schemaType = SProduct(list), name = Some(sName))
      }
    }

  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F] {
    //
  }

}
