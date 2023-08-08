package server

import cats.effect.IO
import com.example.protos.hello._
import fs2.*
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all.*
import cats.effect.IOApp
import cats.effect.kernel.Resource
import cats.effect.ExitCode

class ExampleImplementation extends GreeterFs2Grpc[IO, Metadata] {
  override def sayHello(request: HelloRequest,
                        clientHeaders: Metadata): IO[HelloReply] = {
    IO(HelloReply("Request name is: " + request.name))
  }

  override def sayHelloStream(
      request: Stream[IO, HelloRequest],
      clientHeaders: Metadata): Stream[IO, HelloReply] = {
    request.evalMap(req => sayHello(req, clientHeaders))
  }
}

object Server:
    val helloService: Resource[IO, ServerServiceDefinition] = 
        GreeterFs2Grpc.bindServiceResource[IO](new ExampleImplementation)

    def runServer(service: ServerServiceDefinition) = NettyServerBuilder
        .forPort(9999)
        .addService(service)
        .resource[IO]
        .evalMap(server => IO(server.start()))
        .useForever