## What does this PR do?
This PR introduces the `MultiProviderComparisonExample.scala` sample under `modules/samples/src/main/scala/org/llm4s/samples/basic/`. 
One of LLM4S's headline features is multi-provider support. However, there was no sample demonstrating running the same prompt against multiple providers and comparing results side-by-side. 
This sample leverages the unified `LLMConnect` API to run the identical prompt across `openai/gpt-4o-mini`, `anthropic/claude-haiku-4-5-latest`, and `gemini/gemini-2.0-flash`. It tracks and prints the unified LLM text output, total token usage metrics, and response latency (in ms) for each provider. It also elegantly checks for configured providers and gracefully skips those without active API keys in the environment.

## Related issue
Fixes #957

## How was this tested?
- Validated logic locally with `sbt "samples/runMain org.llm4s.samples.basic.MultiProviderComparisonExample"` to verify environment fallback and correct LLM completions.
- Verified error handling paths when API keys are absent (the script exits gracefully if no keys are found).
- Ran `sbt scalafmtAll` to ensure Scala formatting compliance.
- Ran `sbt buildAll` locally to guarantee that the sample compiles successfully across all versions and does not break existing builds.

## Checklist

- [x] I have read the [Contributing Guide](https://llm4s.org/reference/contributing)
- [x] PR is small and focused — one change, one reason
- [x] `sbt scalafmtAll` — code is formatted
- [x] `sbt test` — tests pass on Scala 3
- [x] New code includes tests
- [x] No unrelated changes included (branched from `main`, not from another PR)
- [x] Commit messages explain the "why"
