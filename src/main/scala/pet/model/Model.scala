package pet.model

import io.circe.{Decoder, Encoder}
import net.scalax.simple.codec.LabelledInstalled.Named
import net.scalax.simple.codec.{DefaultModelImplement, GetFieldModel, IndexModel, LabelledInstalled}
import net.scalax.simple.codec.generic.SimpleFromProduct
import net.scalax.simple.codec.to_list_generic.SimpleProduct
import pet.generic.{CirceGeneric, DtoNamed, SProductFieldGetter, SlickDescribe, SlickNamed, SlickOptions, SlickTableRep, ToFieldName}
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

  given [IdM[_]](using Encoder[IdM[Long]]): Cat[IdM, Encoder] = Cat[IdM, Encoder](summon, summon, summon)

  given [IdM[_]](using Decoder[IdM[Long]]): Cat[IdM, Decoder] = Cat[IdM, Decoder](summon, summon, summon)

  given [IdM[_]]: LabelledInstalled[Cat.IDF[IdM]] = LabelledInstalled[Cat.IDF[IdM]].derived(summon, summon)

  given [IdM[_]]: DtoNamed[Cat.IDF[IdM]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[IdM]]].labelled
    DtoNamed[Cat.IDF[IdM]].from(lab.copy(id = "private_id", name = "cat_name", owner = "own_people"))
  }

  given [IdM[_]](using Encoder[IdM[Long]]): Encoder[Cat[IdM, Id]] =
    CirceGeneric.encode(summon, summon, summon[DtoNamed[Cat.IDF[IdM]]].labelled)

  given [IdM[_]](using Decoder[IdM[Long]]): Decoder[Cat[IdM, Id]] =
    CirceGeneric.decode(summon, summon, summon[DtoNamed[Cat.IDF[IdM]]].labelled)

  given Cat[Id, TypedType] = Cat[Id, TypedType](summon, summon, summon)

  given SlickNamed[Cat.IDF[Id]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[Id]]].labelled
    SlickNamed[Cat.IDF[Id]].from(lab.copy(id = "id", name = "name", owner = "owner"))
  }

  given [IdM[_]]: SlickDescribe[Cat.IDF[IdM]] = {
    val lab = summon[LabelledInstalled[Cat.IDF[IdM]]].labelled
    SlickDescribe[Cat.IDF[IdM]].from(lab.copy(id = "数据库ID(被保护字段)", name = "猫猫的名字", owner = "猫猫的主人"))
  }

  given SlickOptions[Cat.IDF[Id]] = SlickOptions[Cat.IDF[Id]].from(_.copy(id = _.seq(_.AutoInc, _.PrimaryKey), name = _.seq(_.Unique)))

  given SlickTableRep[Cat.IDF[Id]] = SlickTableRep[Cat.IDF[Id]].derived(summon, summon, summon, summon, summon)

  given [IdM[_]](using Schema[IdM[Long]]): Cat[IdM, Schema] = Cat[IdM, Schema](summon, summon, summon)

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

abstract class CatTable[IdM[_]](cons: Tag)(using sv: Shape[? <: FlatShapeLevel, Rep[IdM[Long]], IdM[Long], ?])
    extends Table[Cat[IdM, Id]](cons, "cat") {
  protected def idRep: Cat[Id, Rep] = summon[SlickTableRep[Cat.IDF[Id]]].table(this)
  protected def adaptRep: Cat[IdM, Rep]

  override def * : ProvenShape[Cat[IdM, Id]] =
    Cat.simpleGeneric[IdM, Rep].to(adaptRep) <> (Cat.simpleGeneric[IdM, Id].from, Cat.simpleGeneric[IdM, Id].to)
}
object CatTable {
  given [IdM[_]]: Conversion[CatTable[IdM], Cat[Id, Rep]] = _.idRep
}

class CatTableId(cons: Tag) extends CatTable[Id](cons) {
  protected def adaptRep: Cat[Id, Rep] = idRep
}

class CatTableOption(cons: Tag) extends CatTable[Option](cons) {
  protected def adaptRep: Cat[Option, Rep] = idRep.copy(id = idRep.id.?)
}

object CatTableQuery extends TableQuery[CatTable[Id]](cons => new CatTableId(cons)) {
  object ForInsert extends TableQuery[CatTable[Option]](cons => new CatTableOption(cons))
}
