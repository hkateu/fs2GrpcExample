import cats.effect.{IOApp, ExitCode, IO}
import fs2.Stream
import server.Server
import client.Client
import server.Server.helloService
import scala.concurrent.duration.*

object Main extends IOApp:
    val serverStream = Stream.eval(helloService.use(srvr => Server.runServer(srvr)))
    val clientStream = Stream.sleep[IO](5.millis) ++ Stream.eval(Client.runClient)
    def run(args: List[String]): IO[ExitCode] = serverStream.concurrently(clientStream).compile.toList.as(ExitCode.Success)
