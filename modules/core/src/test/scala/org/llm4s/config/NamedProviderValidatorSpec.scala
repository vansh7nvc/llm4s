package org.llm4s.config

import org.llm4s.error.ConfigurationError
import org.llm4s.types.ProviderModelTypes.*
import org.llm4s.config.ProvidersConfigModel.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NamedProviderValidatorSpec extends AnyFlatSpec with Matchers {

  "AzureValidator" should "mention missing Azure fields by name" in {
    val section = RawNamedProviderSection(
      provider = Some("azure"),
      model = Some("gpt-4"),
      baseUrl = None,
      apiKey = None,
      organization = None,
      endpoint = None,
      apiVersion = None
    )
    val result = NamedProviderValidators.Azure.validate(ProviderName("my-azure"), section)

    result.isLeft shouldBe true
    val err = result.left.toOption.get.asInstanceOf[ConfigurationError]

    err.message should include("Azure OpenAI provider 'my-azure' is missing required fields")
    err.message should include(
      "- apiKey: set AZURE_API_KEY or provide it in llm4s.conf under providers.my-azure.apiKey"
    )
    err.message should include("- endpoint: the model deployment name in your Azure OpenAI resource")
  }

  "OpenAIValidator" should "mention missing OpenAI fields by name" in {
    val section = RawNamedProviderSection(
      provider = Some("openai"),
      model = Some("gpt-4"),
      baseUrl = None,
      apiKey = None,
      organization = None,
      endpoint = None,
      apiVersion = None
    )
    val result = NamedProviderValidators.OpenAI.validate(ProviderName("my-openai"), section)

    result.isLeft shouldBe true
    val err = result.left.toOption.get.asInstanceOf[ConfigurationError]

    err.message should include("OpenAI provider 'my-openai' is missing required fields")
    err.message should include(
      "- apiKey: set OPENAI_API_KEY or provide it in llm4s.conf under providers.my-openai.apiKey"
    )
  }

  "OllamaValidator" should "mention missing Ollama fields by name" in {
    val section = RawNamedProviderSection(
      provider = Some("ollama"),
      model = Some("llama3"),
      baseUrl = None,
      apiKey = None,
      organization = None,
      endpoint = None,
      apiVersion = None
    )
    val result = NamedProviderValidators.Ollama.validate(ProviderName("my-ollama"), section)

    result.isLeft shouldBe true
    val err = result.left.toOption.get.asInstanceOf[ConfigurationError]

    err.message should include("Ollama provider 'my-ollama' is missing required fields")
    err.message should include("- baseUrl: set OLLAMA_BASE_URL (e.g. http://localhost:11434)")
  }
}
