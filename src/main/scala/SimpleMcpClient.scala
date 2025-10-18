package org.carlos.app

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.sys.process._
import scala.io.StdIn
import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}

// MCP JSON-RPC message types
case class McpRequest(
  jsonrpc: String = "2.0",
  id: Int,
  method: String,
  params: Option[Json] = None
)

case class McpResponse(
  jsonrpc: String,
  id: Int,
  result: Option[Json],
  error: Option[McpError]
)

case class McpError(
  code: Int,
  message: String,
  data: Option[Json] = None
)

/**
 * Simple MCP Client for Scala
 * Connects to MCP servers via stdio (standard input/output)
 */
object SimpleMcpClient {

  /**
   * Connect to MCP server via stdio (subprocess)
   * This is the most common way MCP servers are run
   */
  def connect(command: String): Unit = {
    println(s"Connecting to MCP server: $command")
    println("=" * 50)

    // Start the MCP server process
    val processBuilder = Process(command)
    val process = processBuilder.run(
      new ProcessIO(
        // Handle stdin - send requests to server
        stdin => {
          val writer = new BufferedWriter(new OutputStreamWriter(stdin))

          try {
            // Send initialize request
            val initRequest = McpRequest(
              id = 1,
              method = "initialize",
              params = Some(Json.obj(
                "protocolVersion" -> Json.fromString("2024-11-05"),
                "clientInfo" -> Json.obj(
                  "name" -> Json.fromString("scala-mcp-client"),
                  "version" -> Json.fromString("1.0.0")
                ),
                "capabilities" -> Json.obj()
              ))
            )

            println(s"Sending initialize request...")
            writer.write(initRequest.asJson.noSpaces)
            writer.write("\n")
            writer.flush()

            // Wait a bit for response
            Thread.sleep(2000)

            // Send initialized notification
            val initializedNotification = Json.obj(
              "jsonrpc" -> Json.fromString("2.0"),
              "method" -> Json.fromString("notifications/initialized")
            )

            println(s"Sending initialized notification...")
            writer.write(initializedNotification.noSpaces)
            writer.write("\n")
            writer.flush()

            // Keep the stream open for a bit
            Thread.sleep(3000)

          } catch {
            case e: Exception =>
              println(s"Error writing to stdin: ${e.getMessage}")
          } finally {
            writer.close()
          }
        },
        // Handle stdout - read responses from server
        stdout => {
          val reader = new BufferedReader(new InputStreamReader(stdout))
          try {
            var line = reader.readLine()
            while (line != null) {
              println(s"\nReceived from server:")
              println(line)

              // Try to parse the response
              decode[McpResponse](line) match {
                case Right(response) =>
                  println(s"Response ID: ${response.id}")
                  response.result.foreach(r => println(s"Result:\n${r.spaces2}"))
                  response.error.foreach(e => println(s"Error: ${e.message}"))
                case Left(error) =>
                  println(s"Could not parse as response: $error")
              }

              line = reader.readLine()
            }
          } catch {
            case e: Exception =>
              println(s"Error reading stdout: ${e.getMessage}")
          } finally {
            reader.close()
          }
        },
        // Handle stderr - print errors
        stderr => {
          val reader = new BufferedReader(new InputStreamReader(stderr))
          try {
            var line = reader.readLine()
            while (line != null) {
              println(s"[Server Error] $line")
              line = reader.readLine()
            }
          } catch {
            case e: Exception =>
              println(s"Error reading stderr: ${e.getMessage}")
          } finally {
            reader.close()
          }
        }
      )
    )

    // Wait for process to complete
    val exitCode = process.exitValue()
    println(s"\nProcess exited with code: $exitCode")
  }
}

/**
 * Main app to run the simple MCP client
 */
object SimpleMcpClientApp extends App {
  println("=== Simple Scala MCP Client ===\n")

  // Example: Connect to an MCP server
  // Uncomment one of the following examples:

  // Example 1: Connect to the MCP "everything" server (requires Node.js)
  // SimpleMcpClient.connect("npx -y @modelcontextprotocol/server-everything")

  // Example 2: Connect to the MCP filesystem server
  // SimpleMcpClient.connect("npx -y @modelcontextprotocol/server-filesystem /path/to/directory")

  // Example 3: Connect to a Python MCP server
  // SimpleMcpClient.connect("uvx mcp-server-fetch")

  println("To use this client, uncomment one of the examples above")
  println("and make sure you have the required MCP server installed.\n")
  println("Common MCP servers:")
  println("  - Node.js servers: npx -y @modelcontextprotocol/server-<name>")
  println("  - Python servers: uvx mcp-server-<name>")
}
