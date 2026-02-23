package org.jetbrains.plugins.scala.compiler.data

import org.jetbrains.jps.incremental.scala.remote.PathTranslator

import java.nio.file.Path

final case class DocumentCompilationData(
  sourcePath: Path,
  sourceContent: String,
  output: Path,
  classpath: Seq[Path],
  scalacOptions: Seq[String]
)

object DocumentCompilationData {

  import Extractors.{StringToPath, StringToPaths, StringToSequence}

  def serialize(data: DocumentCompilationData, translator: PathTranslator): Seq[String] = {
    val DocumentCompilationData(sourcePath, sourceContent, output, classpath, scalacOptions) = data

    import serialization.SerializationUtils.sequenceToString

    val pathToString: Path => String = translator.translate
    val pathsToString: Seq[Path] => String = paths => sequenceToString(paths.map(pathToString))

    Seq(
      pathToString(sourcePath),
      sourceContent,
      pathToString(output),
      pathsToString(classpath),
      sequenceToString(scalacOptions)
    )
  }

  def deserialize(strings: Seq[String]): Either[String, DocumentCompilationData] = strings match {
    case Seq(
      StringToPath(sourcePath),
      sourceContent,
      StringToPath(output),
      StringToPaths(classpath),
      StringToSequence(scalacOptions)
    ) => Right(DocumentCompilationData(sourcePath, sourceContent, output, classpath, scalacOptions))
    case args => Left(s"The arguments don't match the expected shape of CompilerData: ${args.mkString("[", ",", "]")}")
  }
}
