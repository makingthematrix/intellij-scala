package org.jetbrains.plugins.scala.compiler.data

import org.jetbrains.jps.incremental.scala.remote.PathTranslator

import java.nio.file.Path

final case class ComputeStampsArguments(
  outputFiles: Seq[Path],
  analysisFile: Path
) {
  def asStrings(translator: PathTranslator): Seq[String] = {
    import org.jetbrains.plugins.scala.compiler.data.serialization.SerializationUtils.sequenceToString
    val pathToString: Path => String = translator.translate
    val pathsToString: Seq[Path] => String = paths => sequenceToString(paths.map(pathToString))

    Seq(
      pathsToString(outputFiles),
      pathToString(analysisFile)
    )
  }
}

object ComputeStampsArguments {
  import Extractors.{StringToPath, StringToPaths}

  def parse(arguments: Seq[String]): Option[ComputeStampsArguments] = arguments match {
    case Seq(StringToPaths(outputFiles), StringToPath(analysisFile)) =>
      Some(ComputeStampsArguments(outputFiles, analysisFile))
    case _ =>
      None
  }
}
