package pet.server

import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.IO
import org.http4s.HttpRoutes
import pet.generic.{GetFieldModel, IndexModel}

import pet.model.Cat
import sttp.tapir.json.circe.*
import cats.Id

def countCharacters(s: String): IO[Either[Unit, Cat[Option, Id]]] =
  IO.pure(Right[Unit, Cat[Option, Id]](Cat[Option, Id](Option.empty, "Tony", "Marry")))

val countCharactersEndpoint: PublicEndpoint[String, Unit, Cat[Option, Id], Any] = {
  endpoint.in(stringBody).out(jsonBody[Cat[Option, Id]])
}

val countCharactersRoutes: HttpRoutes[IO] =
  Http4sServerInterpreter[IO]().toRoutes(countCharactersEndpoint.serverLogic(countCharacters _))

@main
def main = {
  println(implicitly[Schema[Cat[Option, Id]]])
  println(implicitly[IndexModel[Cat.IDF[Option]]].model)

  val getName = implicitly[GetFieldModel[Cat.IDF[Option]]].getFieldModel[Id]
  println(getName.owner(Cat[Option, Id](Option.empty, "Tony", "Marry")))
}
