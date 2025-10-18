ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaV1",
    idePackagePrefix := Some("org.carlos.app"),
    // Library dependencies required by the codebase
    // Note: version numbers may be adjusted to match your environment
    // if you encounter resolution issues.
    libraryDependencies ++= Seq(
      // Breeze (linear algebra: DenseVector, etc.)
      "org.scalanlp" %% "breeze" % "2.2.0",

      // Parquet4s core API
      "com.github.mjakubowski84" %% "parquet4s-core" % "2.14.1",

      // Hadoop Configuration class (org.apache.hadoop.conf.Configuration)
      "org.apache.hadoop" % "hadoop-common" % "3.3.6",

      // Circe for JSON (used in SimpleMcpClient)
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6"
    )
  )
