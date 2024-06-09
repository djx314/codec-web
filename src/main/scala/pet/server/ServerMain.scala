package pet.server

import sttp.tapir._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.IO
import org.http4s.HttpRoutes

import sttp.tapir.generic.auto._

import pet.model.Cat

import sttp.tapir.json.circe._

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
}
