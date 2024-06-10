package pet.model

import io.circe.{Decoder, Encoder}
import net.scalax.simple.codec.LabelledInstalled.Named
import net.scalax.simple.codec.{CirceGeneric, DefaultModelImplement, FillIdentity, LabelledInstalled}
import net.scalax.simple.codec.generic.SimpleFromProduct
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import pet.generic.{DtoNamed, IndexModel, SlickDescribe, SlickNamed, SlickOptions, SlickTableRep, ToFieldName}
import slick.ast.TypedType
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.ProvenShape
import sttp.tapir.Schema

case class Cat[IdM[_], F[_]](id: F[IdM[Long]], name: F[String], owner: F[String])

object Cat {

  type IDCat[IdM[_]] = [F[_]] =>> Cat[IdM, F]
  type Id            = [t] =>> t

  def simpleGeneric[IdM[_], I[_]] = SimpleFromProduct[IDCat[IdM], I].derived.generic

  given [IdM[_]]: SimpleProduct.Appender[IDCat[IdM]] = new SimpleProduct.Appender.Impl[IDCat[IdM]] {
    override def impl[M1[_, _, _], M2[_], M3[_], M4[_]] = _.derived2(
      simpleGeneric[IdM, Id],
      simpleGeneric[IdM, M2],
      simpleGeneric[IdM, M3],
      simpleGeneric[IdM, M4]
    )(_.generic)
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Cat[IdM, Encoder] = FillIdentity[IDCat[IdM], Encoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Encoder, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]](using Decoder[IdM[Long]]): Cat[IdM, Decoder] = FillIdentity[IDCat[IdM], Decoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Decoder, DefaultModelImplement.type]#Type])(_.generic)
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

  given [IdM[_]](using TypedType[IdM[Long]]): Cat[IdM, TypedType] = FillIdentity[IDCat[IdM], TypedType]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[TypedType, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]]: SlickNamed[IDCat[IdM]] = {
    val lab = summon[LabelledInstalled[IDCat[IdM]]].labelled
    SlickNamed[IDCat[IdM]].from(lab.copy(id = "id", name = "name", owner = "owner"))
  }

  given [IdM[_]]: SlickDescribe[IDCat[IdM]] = {
    val lab = summon[LabelledInstalled[IDCat[IdM]]].labelled
    SlickDescribe[IDCat[IdM]].from(lab.copy(id = "Database id.", name = "The name of the cat.", owner = "The owner of the cat."))
  }

  given [IdM[_]]: SlickOptions[IDCat[IdM]] =
    SlickOptions[IDCat[IdM]].from(_.copy(id = _.seq(_.AutoInc, _.PrimaryKey), name = _.seq(_.Unique)))

  given [IdM[_]](using TypedType[IdM[Long]]): SlickTableRep[IDCat[IdM]] =
    SlickTableRep[IDCat[IdM]].derived(summon, summon, summon, summon, summon)

  given [IdM[_]](using Schema[IdM[Long]]): Cat[IdM, Schema] = FillIdentity[IDCat[IdM], Schema]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Schema, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]]: ToFieldName[IDCat[IdM]] = {
    val name = summon[DtoNamed[IDCat[IdM]]].labelled
    ToFieldName[IDCat[IdM]].derived(summon, name)
  }

  given [IdM[_]]: IndexModel[IDCat[IdM]] = IndexModel[IDCat[IdM]].derived(summon)

}

class CatTable[IdM[_]](cons: Tag)(using TypedType[IdM[Long]], Shape[? <: FlatShapeLevel, Rep[IdM[Long]], IdM[Long], ?])
    extends Table[Cat[IdM, Cat.Id]](cons, "cat") {
  val __tableRep: Cat[IdM, Rep] = summon[SlickTableRep[Cat.IDCat[IdM]]].table(this)

  override def * : ProvenShape[Cat[IdM, Cat.Id]] =
    Cat.simpleGeneric[IdM, Rep].to(__tableRep) <> (Cat.simpleGeneric[IdM, Cat.Id].from, Cat.simpleGeneric[IdM, Cat.Id].to)
}

object CatTable {
  given [IdM[_]]: Conversion[CatTable[IdM], Cat[IdM, Rep]] = _.__tableRep
}

object CatTableQuery extends TableQuery[Table[Cat[Cat.Id, Cat.Id]]](cons => new CatTable[Cat.Id](cons)) {
  object ForInsert extends TableQuery[Table[Cat[Option, Cat.Id]]](cons => new CatTable[Option](cons))
}
