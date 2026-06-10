package org.llm4s.samples.mcp

import org.llm4s.mcp.{ MCPServer, MCPServerOptions }
import org.llm4s.toolapi.{ Schema, SafeParameterExtractor, ToolBuilder }
import org.slf4j.LoggerFactory

/**
 * Authenticated MCP Server sample – production-ready configuration.
 *
 * Demonstrates:
 *   - Bearer-token authentication via `MCPServerOptions.apiKey`
 *   - Binding to all interfaces (0.0.0.0) for network-accessible deployments
 *   - Both MCP transports exposed on the same server:
 *       • Streamable HTTP (2025-06-18): POST /mcp
 *       • HTTP+SSE        (2024-11-05): GET  /mcp/sse + POST /mcp/messages
 *
 * === Running ===
 * {{{
 *   sbt "samples/runMain org.llm4s.samples.mcp.AuthenticatedMCPServerSample"
 * }}}
 *
 * === Claude Desktop configuration ===
 * Add the following to `~/Library/Application Support/Claude/claude_desktop_config.json`
 * (macOS) or `%APPDATA%\Claude\claude_desktop_config.json` (Windows):
 *
 * {{{
 * {
 *   "mcpServers": {
 *     "llm4s-demo": {
 *       "url": "http://127.0.0.1:8080/mcp/sse",
 *       "headers": {
 *         "Authorization": "Bearer my-secret-key-change-me"
 *       }
 *     }
 *   }
 * }
 * }}}
 *
 * Restart Claude Desktop after editing the config file.
 *
 * === Cursor / VS Code MCP configuration ===
 * Add to `.cursor/mcp.json` or `.vscode/mcp.json`:
 * {{{
 * {
 *   "servers": {
 *     "llm4s-demo": {
 *       "type": "sse",
 *       "url": "http://127.0.0.1:8080/mcp/sse",
 *       "headers": {
 *         "Authorization": "Bearer my-secret-key-change-me"
 *       }
 *     }
 *   }
 * }
 * }}}
 */
object AuthenticatedMCPServerSample {
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    // In production, load this from an environment variable or secrets manager.
    val apiKey = sys.env.getOrElse("MCP_API_KEY", "my-secret-key-change-me")
    val port   = sys.env.get("MCP_PORT").flatMap(_.toIntOption).getOrElse(8080)

    // ── Define tools ────────────────────────────────────────────────────────

    val echoTool = ToolBuilder[Map[String, Any], String](
      name = "echo",
      description = "Echo a message back to the caller",
      schema = Schema
        .`object`[Map[String, Any]]("Echo parameters")
        .withProperty(Schema.property("message", Schema.string("The text to echo")))
    ).withHandler((params: SafeParameterExtractor) => params.getString("message").map(msg => s"Echo: $msg"))
      .buildSafe()
      .fold(
        err => throw new RuntimeException(s"Failed to build echo tool: ${err.formatted}"),
        identity
      )

    val reverseTool = ToolBuilder[Map[String, Any], String](
      name = "reverse",
      description = "Reverse a string",
      schema = Schema
        .`object`[Map[String, Any]]("Reverse parameters")
        .withProperty(Schema.property("text", Schema.string("The text to reverse")))
    ).withHandler((params: SafeParameterExtractor) => params.getString("text").map(_.reverse))
      .buildSafe()
      .fold(
        err => throw new RuntimeException(s"Failed to build reverse tool: ${err.formatted}"),
        identity
      )

    // ── Start server ─────────────────────────────────────────────────────────

    val options = MCPServerOptions(
      port = port,
      path = "/mcp",
      name = "llm4s-demo",
      version = "1.0.0",
      apiKey = Some(apiKey),
      // Bind to 0.0.0.0 so both localhost and LAN clients can connect.
      // Safe here because bearer-token auth is enabled above.
      host = "0.0.0.0"
    )

    val server = new MCPServer(options, Seq(echoTool, reverseTool))

    server.start() match {
      case Left(err) =>
        logger.error(s"Failed to start MCP server: ${err.getMessage}", err)
        sys.exit(1)
      case Right(_) =>
        logger.info(s"MCP server started on port $port")
        logger.info("Streamable HTTP (2025-06-18): POST http://127.0.0.1:{port}/mcp")
        logger.info("SSE transport   (2024-11-05): GET  http://127.0.0.1:{port}/mcp/sse")
        logger.info("Claude Desktop config:  url = http://127.0.0.1:{port}/mcp/sse")
        logger.info(s"API key (set MCP_API_KEY env to override): $apiKey")
    }

    sys.addShutdownHook {
      logger.info("Shutting down MCP server...")
      server.stop()
    }

    // Block the main thread until the JVM is terminated
    Thread.currentThread().join()
  }
}
