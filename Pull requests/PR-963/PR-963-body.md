## What does this PR do?
This PR improves the error messages emitted by `NamedProviderValidator` when an LLM provider is misconfigured. Instead of a generic missing configuration error that short-circuits on the first missing field, it now:
- Collects ALL missing required fields for the provider.
- Reports them comprehensively with specific environment variables that need to be set (e.g., `AZURE_API_KEY`, `OPENAI_API_KEY`, etc.).
- Includes examples of the expected format for `baseUrl` and `endpoint` fields, so developers know exactly what to pass.
- Fixes the Azure validator logic to correctly flag `baseUrl` as a required field for that provider type.

## Related issue
Fixes #963

## How was this tested?
- Added `NamedProviderValidatorSpec.scala` with comprehensive tests verifying the specific error format strings for Azure, OpenAI, and Ollama.
- Ran `sbt "core/testOnly *NamedProviderValidatorSpec"` locally to confirm the exact exception message contents.
- Validated with `llm4s-pr-manager` verifying overall pipeline integrity (`sbt buildAll`), confirming that these modifications do not break any builds and compile effectively across Scala versions.

## Checklist

- [x] I have read the [Contributing Guide](https://llm4s.org/reference/contributing)
- [x] PR is small and focused — one change, one reason
- [x] `sbt scalafmtAll` — code is formatted
- [x] `sbt test` — tests pass on Scala 3
- [x] New code includes tests
- [x] No unrelated changes included (branched from `main`, not from another PR)
- [x] Commit messages explain the "why"
