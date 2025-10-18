package org.carlos.app
//
import _root_.scala.math
//
import scala.collection.Seq
import scala.collection.immutable.*
import scala.collection.mutable.ArraySeq
//
import scala.io.Source
import scala.util.{Try, Success, Failure}
//
import org.slf4j.Logger

import java.sql.DriverManager
import breeze.linalg.DenseVector

//define function to check if a number does not divide another number
val notDivides = (Ntest: Int, nDiv: Int) => !(Ntest % nDiv == 0)

//read 'stockPVN.csv' as a list of strings
val stockPVN: Seq[String] = scala.io.Source.fromFile("src/main/scala/ejemplo/stockPVN.csv").getLines.toList

//Define a range from 2 to the square root of the number to test
def range(Ntest: Int): Range = Range(2,1+math.sqrt(Ntest).toInt)

//Define a function to check if a number is not divisible by any number in a specific range.
val isPrime = (Ntest: Int) =>  range(Ntest = Ntest).forall(notDivides(Ntest, _))

case class Series(index: Vector[Double], data: Vector[Double]) {
  def apply(i: Double): Double = {
    data(index.indexOf(i))
  }
}

case class DataFrame(index: Vector[Double], columns: Vector[String], data: Vector[Series]) {
  def apply(i: Double, j: String): Double = {
    data(columns.indexOf(j))(i)
  }
}

def readSeries(filename: String): Option[Series] = {
  Try {
    val lines = Source.fromFile(filename).getLines().toVector
    val index = lines.map(_.split(",")(0).toDouble)
    val data = lines.map(_.split(",")(1).toDouble)
    Series(index, data)
  }.toOption
}



object holaMundo extends App {
  println("Ejemplo de App de Scala...");
  var Ntest: Int = 2;
  //print curent working directory
  println("Current working directory: " + System.getProperty("user.dir"))
  println(stockPVN);

  // Example: Read stockPVN.csv into DenseVector
  println("\n--- Reading CSV into DenseVector ---")

  while (true) {
    val input = scala.io.StdIn.readLine("Enter a number: ")
    println(s"You entered: $input")
    if (isPrime(input.toInt)) {
      println(s"$input is prime") ;
    }
  }
  while (false) {
    if (isPrime(Ntest)) {
      println(s"$Ntest") ;
    }
    Ntest = Ntest + 1 ;
  }
  val series = readSeries("src/main/scala/ejemplo/stockPVN.csv")
}

val jenksScala = (arr: Vector[Double], weight: Vector[Double], nClass: Int) => {
}
//
def validateInput(values: Seq[Double], nClasses: Any): Int = {

  // Check input to ensure it's a sequence of numbers
  if (!values.isInstanceOf[Iterable[_]] || values.isInstanceOf[String] || values.isInstanceOf[Array[Byte]]) then
    throw new IllegalArgumentException("A sequence of numbers is expected")

  // Check that nClasses is an integer or convertible to integer
  val nClassesInt = nClasses match {
    case n: Int => n
    case n: Double if n.isWhole => n.toInt
    case _ => throw new IllegalArgumentException(
      s"Number of classes must be a positive integer: expected an instance of 'Int' but found ${nClasses.getClass.getName}"
    )
  }

  // Check that all values in the array are finite
  if (values.exists(v => v.isInfinite || v.isNaN)) then
    throw new IllegalArgumentException("All values must be finite")

  // Check for unique values and validate nClasses range
  val uniqueValues = values.distinct
  if (nClassesInt > uniqueValues.length || nClassesInt < 1) then
    throw new IllegalArgumentException(
      "Number of classes must be an integer greater than or equal to 1 and " +
        "less than or equal to the number of unique values."
    )

  nClassesInt
}
