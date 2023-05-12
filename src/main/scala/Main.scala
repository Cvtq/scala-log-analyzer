import analyzer.LogAnalyzer

@main
def main(inputFile: String, outputFile: String): Unit = {
  val analyzer: LogAnalyzer = new LogAnalyzer()
  analyzer.analyzeFile(inputFile)
  analyzer.createOutputFile(outputFile)
}