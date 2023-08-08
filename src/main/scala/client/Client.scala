package client

import cats.effect.{IO,IOApp,ExitCode}
import com.example.protos.hello.*
import fs2.*
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import fs2.grpc.syntax.all.*
import cats.effect.kernel.Resource

object Client:
  val managedChannelResource: Resource[IO, ManagedChannel] = 
    NettyChannelBuilder
    .forAddress("127.0.0.1", 9999)
    .usePlaintext()
    .resource[IO]

  def runProgram(helloStub: GreeterFs2Grpc[IO, Metadata]): IO[Unit] = {
    for {
      response <- helloStub.sayHello(HelloRequest("John Doe"), new Metadata())
      _ <- IO(println(response.message))
    } yield ()
  }

  def runProgram2(helloStub: GreeterFs2Grpc[IO, Metadata]): IO[Unit] = {
    for {
      response <- helloStub.sayHelloStream(
        Stream(
          HelloRequest("John Doe"),
          HelloRequest("Chris Brown"),
          HelloRequest("Andrew Cohen"),
          HelloRequest("Crocodile Dundee")
        ), 
        new Metadata())
      _ <- Stream.eval(IO(println(response.message)))
    } yield ()
  }.compile.drain

  val runClient: IO[Unit] = managedChannelResource
    .flatMap(ch => GreeterFs2Grpc.stubResource[IO](ch))
    .use(runProgram2)