package pet.server

import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.IO
import org.http4s.HttpRoutes
import pet.generic.IndexModel
import sttp.tapir.generic.auto.*
import pet.model.Cat
import sttp.tapir.json.circe.*

def countCharacters(s: String): IO[Either[Unit, Cat[Option, Cat.Id]]] =
  IO.pure(Right[Unit, Cat[Option, Cat.Id]](Cat[Option, Cat.Id](Option.empty, "Tony", "Marry")))

val countCharactersEndpoint: PublicEndpoint[String, Unit, Cat[Option, Cat.Id], Any] = {
  endpoint.in(stringBody).out(jsonBody[Cat[Option, Cat.Id]])
}

val countCharactersRoutes: HttpRoutes[IO] =
  Http4sServerInterpreter[IO]().toRoutes(countCharactersEndpoint.serverLogic(countCharacters _))

@main
def main = {
  println(implicitly[Schema[Cat[Option, Cat.Id]]])
  println(implicitly[IndexModel[Cat.IDCat[Option]]].model)
}
