import scala.io.Source
import java.io._
import java.util.zip._

class LogAnalyzer {

  def analyzeFile(inputFile: String): Unit = {
    if (inputFile.endsWith(".zip")) {
      val zipIn: ZipInputStream = new ZipInputStream(new FileInputStream(inputFile))
      var entry: ZipEntry = zipIn.getNextEntry()
      while (entry != null) {
        if (!entry.isDirectory && (entry.getName.endsWith(".txt") || entry.getName.endsWith(".log"))) {
          readInputStream(zipIn)
        }
        entry = zipIn.getNextEntry()
      }
      zipIn.close()
    }
    else
    {
      val inputStream = new FileInputStream(inputFile)
      readInputStream(inputStream)
      inputStream.close()
    }
  }

  def createOutputFile(outputFile: String): Unit = {
    val writer = new PrintWriter(new File(outputFile))
    writer.println("IP Addresses:")
    ipCount.foreach { case (ip, count) => writer.println(s"$ip: $count") }
    writer.println("Logins:")
    loginCount.foreach { case (login, count) => writer.println(s"$login: $count") }
    writer.close()
  }

  private val ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".r
  private val loginRegex = "login=(\\w+)".r

  private val ipCount = collection.mutable.Map[String, Int]()
  private val loginCount = collection.mutable.Map[String, Int]()

  private def readInputStream(inputStream: InputStream) : Unit = {
    val lines = Source.fromInputStream(inputStream).getLines()

    for (line <- lines) {
      val ip = ipRegex.findFirstIn(line)
      if (ip.isDefined) {
        val ipString = ip.get
        ipCount(ipString) = ipCount.getOrElse(ipString, 0) + 1
      }

      val login = loginRegex.findFirstMatchIn(line)
      if (login.isDefined) {
        val loginString = login.get.group(1)
        loginCount(loginString) = loginCount.getOrElse(loginString, 0) + 1
      }
    }
  }
}

@main
def main(inputFile: String, outputFile: String): Unit = {
  val analyzer: LogAnalyzer = new LogAnalyzer()
  analyzer.analyzeFile(inputFile)
  analyzer.createOutputFile(outputFile)
}