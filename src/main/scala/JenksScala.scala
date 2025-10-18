//
package org.carlos.app
//
import breeze.linalg._
import breeze.numerics._
import breeze.stats._

import com.github.mjakubowski84.parquet4s._
import org.apache.hadoop.conf.Configuration

object JenksScala {
  /**
   * Implementation of Jenks Natural Breaks algorithm for 1D data
   *
   * The Jenks optimization method (also called natural breaks classification)
   * minimizes variance within classes and maximizes variance between classes.
   *
   * @param data       The input data vector to classify
   * @param numClasses The number of classes/breaks to create
   * @return Array of break points (upper bounds for each class)
   */
  def jenksBreaks(data: DenseVector[Double], numClasses: Int): Array[Double] = {
    require(numClasses > 0 && numClasses < data.length,
      s"Number of classes must be between 1 and ${data.length - 1}")

    val sortedData = data.toArray.sorted
    val n = sortedData.length

    // Initialize matrices for dynamic programming
    // lowerClassLimits(i)(j) = lower class limit for class j with i data points
    val lowerClassLimits = Array.ofDim[Int](n + 1, numClasses + 1)

    // varianceCombinations(i)(j) = variance for optimal classification of i data points into j classes
    val varianceCombinations = Array.ofDim[Double](n + 1, numClasses + 1)

    // Initialize with large values
    for (i <- 1 to n; j <- 1 to numClasses) {
      varianceCombinations(i)(j) = Double.MaxValue
    }

    // One class has zero variance
    for (i <- 1 to n) {
      varianceCombinations(i)(1) = 0.0
      lowerClassLimits(i)(1) = 1
    }

    // Calculate variance for all possible partitions
    for (i <- 2 to n) {
      val data_i = DenseVector(sortedData.slice(0, i))
      val sum_i = sum(data_i)
      val sumSquares_i = sum(data_i *:* data_i)

      for (j <- 2 to math.min(i, numClasses)) {
        // Try all possible lower class limits
        for (m <- j - 1 until i) {
          val data_m = DenseVector(sortedData.slice(0, m))
          val sum_m = sum(data_m)
          val sumSquares_m = sum(data_m *:* data_m)

          // Calculate variance for the current class (from m to i)
          val classSize = i - m
          val classSum = sum_i - sum_m
          val classSumSquares = sumSquares_i - sumSquares_m

          val variance = classSumSquares - (classSum * classSum) / classSize

          val totalVariance = varianceCombinations(m)(j - 1) + variance

          if (totalVariance < varianceCombinations(i)(j)) {
            varianceCombinations(i)(j) = totalVariance
            lowerClassLimits(i)(j) = m + 1
          }
        }
      }
    }

    // Extract the break points
    val breaks = new Array[Double](numClasses)
    var k = n

    for (j <- numClasses to 2 by -1) {
      val lowerLimit = lowerClassLimits(k)(j) - 1
      breaks(j - 1) = sortedData(k - 1)
      k = lowerLimit
    }

    breaks(0) = sortedData(k - 1)
    breaks
  }

  /**
   * Classify data points into classes based on Jenks breaks
   *
   * @param data   The input data vector
   * @param breaks The break points from jenksBreaks
   * @return Vector of class indices (0-based)
   */
  def classify(data: DenseVector[Double], breaks: Array[Double]): DenseVector[Int] = {
    data.map { value =>
      breaks.indexWhere(breakPoint => value <= breakPoint) match {
        case -1 => breaks.length - 1
        case idx => idx
      }
    }
  }
}
