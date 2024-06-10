package pet.server

import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import cats.syntax.all.*
import pet.generic.{GetFieldModel, IndexModel}
import pet.model.Cat
import sttp.tapir.json.circe.*
import cats.Id
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._

def countCharacters(s: String): IO[Either[Unit, Cat[Option, Id]]] =
  IO.pure(Right[Unit, Cat[Option, Id]](Cat[Option, Id](Option.empty, s, "Marry")))

val countCharactersEndpoint: PublicEndpoint[String, Unit, Cat[Option, Id], Any] = {
  endpoint.in("hello" / "cat").in(stringBody).out(jsonBody[Cat[Option, Id]])
}

val swaggerRouterImpl = SwaggerInterpreter().fromEndpoints[IO](List(countCharactersEndpoint), "Cat App", "1.0")

val docRouter: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(swaggerRouterImpl)
val countCharactersRoutes: HttpRoutes[IO] =
  Http4sServerInterpreter[IO]().toRoutes(countCharactersEndpoint.serverLogic(countCharacters))

val http4sRoutes: HttpRoutes[IO] = docRouter <+> countCharactersRoutes

val server =
  EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(http4sRoutes.orNotFound).build

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = server.use(_ => IO.never).as(ExitCode.Success)

}
