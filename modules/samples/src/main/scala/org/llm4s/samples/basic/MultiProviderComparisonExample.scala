package org.llm4s.samples.basic

import org.llm4s.config.Llm4sConfig
import org.llm4s.llmconnect.LLMConnect
import org.llm4s.llmconnect.model._

/**
 * Example demonstrating how to run the same prompt against multiple providers side by side.
 * This highlights the unified API where the same code works across OpenAI, Anthropic, Gemini, etc.
 *
 * To run:
 * export OPENAI_API_KEY=sk-...
 * export ANTHROPIC_API_KEY=sk-ant-...
 * export GOOGLE_API_KEY=...
 * sbt "samples/runMain org.llm4s.samples.basic.MultiProviderComparisonExample"
 */
object MultiProviderComparisonExample {
  def main(args: Array[String]): Unit =
    // Initialize the model registry service required by LLMConnect
    Llm4sConfig.modelRegistryService() match {
      case Left(err) =>
        println(s"Failed to initialize model registry: ${err.message}")
      case Right(registryService) =>
        given org.llm4s.model.ModelRegistryService = registryService

        // Try to load multiple providers from environment — skip gracefully if a key is not set
        val providers = Seq(
          "openai/gpt-4o-mini",
          "anthropic/claude-haiku-4-5-latest",
          "gemini/gemini-2.0-flash"
        ).flatMap(name => Llm4sConfig.provider(name).toOption)

        if (providers.isEmpty) {
          println(
            "No providers configured. Please set at least one of OPENAI_API_KEY, ANTHROPIC_API_KEY, or GOOGLE_API_KEY."
          )
          return
        }

        val prompt = "Explain what makes Scala good for building AI applications in 2 sentences."
        println(s"Prompt: $prompt\n")

        providers.foreach { config =>
          val start = System.currentTimeMillis()

          LLMConnect.getClient(config).flatMap(_.complete(Conversation(Seq(UserMessage(prompt))))) match {
            case Right(completion) =>
              val ms     = System.currentTimeMillis() - start
              val tokens = completion.usage.map(_.totalTokens).getOrElse(0)
              println(s"${config.provider}: ${completion.asText.trim} (tokens: $tokens, ${ms}ms)\n")
            case Left(err) =>
              println(s"${config.provider}: ERROR - ${err.message}\n")
          }
        }
    }
}
