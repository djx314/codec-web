package pet.model

import io.circe.{Decoder, Encoder}
import net.scalax.simple.codec.LabelledInstalled.Named
import net.scalax.simple.codec.{CirceGeneric, DefaultModelImplement, FillIdentity, LabelledInstalled}
import net.scalax.simple.codec.generic.SimpleFromProduct
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import pet.generic.{
  DtoNamed,
  GetFieldModel,
  IndexModel,
  SProductFieldGetter,
  SlickDescribe,
  SlickNamed,
  SlickOptions,
  SlickTableRep,
  ToFieldName
}
import slick.ast.TypedType
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.ProvenShape
import sttp.tapir.Schema
import cats.Id
import sttp.tapir.Schema.SName

case class Cat[IdM[_], F[_]](id: F[IdM[Long]], name: F[String], owner: F[String])

object Cat {

  type IDF[IdM[_]] = [F[_]] =>> Cat[IdM, F]

  def simpleGeneric[IdM[_], I[_]] = SimpleFromProduct[Cat.IDF[IdM], I].derived.generic

  given [IdM[_]]: SimpleProduct.Appender[Cat.IDF[IdM]] = new SimpleProduct.Appender.Impl[Cat.IDF[IdM]] {
    override def impl[M1[_, _, _], M2[_], M3[_], M4[_]] = _.derived2(
      simpleGeneric[IdM, Id],
      simpleGeneric[IdM, M2],
      simpleGeneric[IdM, M3],
      simpleGeneric[IdM, M4]
    )(_.generic)
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Cat[IdM, Encoder] = FillIdentity[Cat.IDF[IdM], Encoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Encoder, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]](using Decoder[IdM[Long]]): Cat[IdM, Decoder] = FillIdentity[Cat.IDF[IdM], Decoder]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Decoder, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]]: LabelledInstalled[Cat.IDF[IdM]] = LabelledInstalled[Cat.IDF[IdM]].derived(summon, summon)

  given [IdM[_]]: DtoNamed[Cat.IDF[IdM]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[IdM]]].labelled
    DtoNamed[Cat.IDF[IdM]].from(lab.copy(id = "private_id", name = "cat_name", owner = "own_people"))
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Encoder[Cat[IdM, Id]] =
    CirceGeneric.encodeModelImpl(summon, summon, summon[DtoNamed[Cat.IDF[IdM]]].labelled)

  given [IdM[_]](using Decoder[IdM[Long]]): Decoder[Cat[IdM, Id]] =
    CirceGeneric.decodeModelImpl(summon, summon, summon[DtoNamed[Cat.IDF[IdM]]].labelled)

  given [IdM[_]](using TypedType[IdM[Long]]): Cat[IdM, TypedType] = FillIdentity[Cat.IDF[IdM], TypedType]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[TypedType, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]]: SlickNamed[Cat.IDF[IdM]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[IdM]]].labelled
    SlickNamed[Cat.IDF[IdM]].from(lab.copy(id = "id", name = "name", owner = "owner"))
  }

  given [IdM[_]]: SlickDescribe[Cat.IDF[IdM]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[IdM]]].labelled
    SlickDescribe[Cat.IDF[IdM]].from(lab.copy(id = "数据库ID(被保护字段)", name = "猫猫的名字", owner = "猫猫的主人"))
  }

  given [IdM[_]]: SlickOptions[Cat.IDF[IdM]] =
    SlickOptions[Cat.IDF[IdM]].from(_.copy(id = _.seq(_.AutoInc, _.PrimaryKey), name = _.seq(_.Unique)))

  given [IdM[_]](using TypedType[IdM[Long]]): SlickTableRep[Cat.IDF[IdM]] =
    SlickTableRep[Cat.IDF[IdM]].derived(summon, summon, summon, summon, summon)

  given [IdM[_]](using Schema[IdM[Long]]): Cat[IdM, Schema] = FillIdentity[Cat.IDF[IdM], Schema]
    .derived2(simpleGeneric[IdM, FillIdentity.WithPoly[Schema, DefaultModelImplement.type]#Type])(_.generic)
    .model(summon)

  given [IdM[_]]: ToFieldName[Cat.IDF[IdM]] = {
    val name = summon[DtoNamed[Cat.IDF[IdM]]].labelled
    ToFieldName[Cat.IDF[IdM]].derived(summon, name)
  }

  given [IdM[_]]: IndexModel[Cat.IDF[IdM]] = IndexModel[Cat.IDF[IdM]].derived(summon)

  given [IdM[_]]: GetFieldModel[Cat.IDF[IdM]] = GetFieldModel[Cat.IDF[IdM]].derived(summon, summon)

  given [IdM[_]](using Schema[IdM[Long]]): SProductFieldGetter[Cat.IDF[IdM]] =
    SProductFieldGetter[Cat.IDF[IdM]].derived(summon, summon, summon, summon, summon)

  given [IdM[_]](using Schema[IdM[Long]]): Schema[Cat[IdM, Id]] =
    summon[SProductFieldGetter[Cat.IDF[IdM]]].objectSchema(SName("pet.model.Cat", Nil))

}

class CatTable[IdM[_]](cons: Tag)(using TypedType[IdM[Long]], Shape[? <: FlatShapeLevel, Rep[IdM[Long]], IdM[Long], ?])
    extends Table[Cat[IdM, Id]](cons, "cat") {
  val __tableRep: Cat[IdM, Rep] = summon[SlickTableRep[Cat.IDF[IdM]]].table(this)

  override def * : ProvenShape[Cat[IdM, Id]] =
    Cat.simpleGeneric[IdM, Rep].to(__tableRep) <> (Cat.simpleGeneric[IdM, Id].from, Cat.simpleGeneric[IdM, Id].to)
}

object CatTable {
  given [IdM[_]]: Conversion[CatTable[IdM], Cat[IdM, Rep]] = _.__tableRep
}

object CatTableQuery extends TableQuery[Table[Cat[Id, Id]]](cons => new CatTable[Id](cons)) {
  object ForInsert extends TableQuery[Table[Cat[Option, Id]]](cons => new CatTable[Option](cons))
}
