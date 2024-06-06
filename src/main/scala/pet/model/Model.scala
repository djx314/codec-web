package pet.model

import io.circe.{Decoder, Encoder}
import net.scalax.simple.codec.LabelledInstalled.Named
import net.scalax.simple.codec.{CirceGeneric, DefaultModelImplement, FillIdentity, LabelledInstalled}
import net.scalax.simple.codec.generic.SimpleFromProduct
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import pet.generic.DtoNamed

case class Cat[IdM[_], F[_]](id: F[IdM[Long]], name: F[String], owner: F[String])

object Cat {

  type IDCat[IdM[_]] = [F[_]] =>> Cat[IdM, F]
  type Id            = [t] =>> t

  def simpleGeneric[IdM[_], I[_]] = SimpleFromProduct[IDCat[IdM], I].derived

  given [IdM[_]]: SimpleProduct.Appender[IDCat[IdM]] = new SimpleProduct.Appender.Impl[IDCat[IdM]] {
    override def impl[M1[_, _, _], M2[_], M3[_], M4[_]] = _.derived2(
      simpleGeneric[IdM, Id].generic,
      simpleGeneric[IdM, M2].generic,
      simpleGeneric[IdM, M3].generic,
      simpleGeneric[IdM, M4].generic
    )(_.generic)
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Cat[IdM, Encoder] = FillIdentity[IDCat[IdM], Encoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Encoder, DefaultModelImplement.type]#Type].generic)(_.generic)
    .model(summon)

  given [IdM[_]](using Decoder[IdM[Long]]): Cat[IdM, Decoder] = FillIdentity[IDCat[IdM], Decoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Decoder, DefaultModelImplement.type]#Type].generic)(_.generic)
    .model(summon)

  given [IdM[_]]: LabelledInstalled[IDCat[IdM]] = LabelledInstalled[IDCat[IdM]].derived(summon, summon)

  given [IdM[_]]: DtoNamed[IDCat[IdM]] = {
    val lab = summon[LabelledInstalled[IDCat[IdM]]].labelled
    DtoNamed[IDCat[IdM]].from(lab.copy(id = "private_id", name = "cat_name", owner = "own_people"))
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Encoder[Cat[IdM, Id]] =
    CirceGeneric.encodeModelImpl(summon, summon, summon[DtoNamed[IDCat[IdM]]].labelled)

  given [IdM[_]](using Decoder[IdM[Long]]): Decoder[Cat[IdM, Id]] =
    CirceGeneric.decodeModelImpl(summon, summon, summon[DtoNamed[IDCat[IdM]]].labelled)

}
