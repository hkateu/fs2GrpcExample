val scala3Version = "3.3.0"

lazy val protobuf =
  project
    .in(file("protobuf"))
    .settings(
      name:= "protobuf",
      scalaVersion := scala3Version
    )
    .enablePlugins(Fs2Grpc)

lazy val root =
  project
    .in(file("."))
    .settings(
      name:= "root",
      scalaVersion := scala3Version,
      libraryDependencies += "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
    )
    .dependsOn(protobuf)

