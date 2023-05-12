package analyzer

import java.io._
import java.util.zip._
import scala.io._
import scala.collection.mutable.Map

class LogAnalyzer {

  private val ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".r
  private val loginRegex = "login=(\\w+)".r

  private val ipCount = Map[String, Int]()
  private val loginCount = Map[String, Int]()

  /**
  Analyzes a file to count occurrences of IP addresses and logins.
  @param inputFile the name of the file to analyze
  @return Unit
  */
  def analyzeFile(inputFile: String): Unit = {
    if (isZipFile(inputFile))
    {
      val zipIn: ZipInputStream = new ZipInputStream(new FileInputStream(inputFile))
      var entry: ZipEntry = zipIn.getNextEntry()
      while (entry != null) {
        val entryName: String = entry.getName()
        if (!entry.isDirectory && (isLogFile(entryName) || isTxtFile(entryName))) {
          readInputStream(zipIn)
        }
        entry = zipIn.getNextEntry()
      }
      zipIn.close()
    }
    else if (isLogFile(inputFile) || isTxtFile(inputFile))
    {
      val inputStream = new FileInputStream(inputFile)
      readInputStream(inputStream)
      inputStream.close()
    }
    else
    {
      val extension = inputFile.split("\\.").lastOption.getOrElse("")
      println(s"Unable to analyze file with extension [$extension]. Check if the extension is correct or choose another file.")
    }
  }

  private def isZipFile(filename: String): Boolean ={
    return filename.endsWith(".zip")
  }

  private def isLogFile(filename: String): Boolean ={
    return filename.endsWith(".log")
  }

  private def isTxtFile(filename: String): Boolean ={
    return filename.endsWith(".txt")
  }

  /**
  Reads the input stream, and counts the number of occurrences of IP addresses and logins in each line.
  @param inputStream the input stream to read
  @return Unit
  */
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

  /**
  Creates an output file with the counts of IP addresses and logins.
  @param outputFile the name of the file to create
  @return Unit
  */
  def createOutputFile(outputFile: String): Unit = {
    val writer = new PrintWriter(new File(outputFile))
    writer.println("IP Addresses:")
    ipCount.foreach { case (ip, count) => writer.println(s"$ip: $count") }
    writer.println("Logins:")
    loginCount.foreach { case (login, count) => writer.println(s"$login: $count") }
    writer.close()
  }
}
