# Add Missing ScalaDocs to Error Hierarchy (#952)

## Related Issue
- Closes [Issue #952](https://github.com/llm4s/llm4s/issues/952)

## Branch
- https://github.com/vansh7nvc/llm4s/tree/docs/add-error-hierarchy-scaladoc

## Motivation and Context
The `org.llm4s.error` package defines a structured error hierarchy for the library, including traits for `RecoverableError` and `NonRecoverableError`. However, several specific error classes were missing proper class-level ScalaDoc documentation. This made it difficult for developers to understand when a specific error is raised, how they should handle it (e.g., whether to implement retry logic), and its exact purpose within the framework. Adding clear ScalaDocs improves developer experience, maintainability, and code readability.

## Detailed Summary of Changes
Added comprehensive, class-level ScalaDoc documentation to the following 7 error types in `org.llm4s.error`:

1. **`AuthenticationError`**: Clarified that it represents a non-recoverable failure due to invalid or missing credentials (e.g., API keys). Handlers should fail fast and prompt for configuration updates rather than retrying.
2. **`ConfigurationError`**: Documented it as a non-recoverable error caused by malformed or missing settings, ensuring the framework halts until the setup is corrected.
3. **`NetworkError`**: Described it as a recoverable error triggered by transient connectivity issues (e.g., connection resets or DNS failures), explicitly suggesting retry with exponential backoff.
4. **`RateLimitError`**: Explained that it indicates HTTP 429 Too Many Requests. Documented it as a recoverable error, instructing consumers to respect the `Retry-After` headers before reattempting.
5. **`TimeoutError`**: Documented as a recoverable error caused by operations exceeding their allotted time limits (e.g., waiting for an LLM response), which can be handled with safe retries.
6. **`ProcessingError`**: Categorized as a non-recoverable runtime failure occurring during data manipulation or text generation (e.g., malformed prompt structures or unparseable JSON), advising against retries.
7. **`ValidationError`**: Detailed as a non-recoverable error resulting from invalid input parameters failing internal constraint checks, requiring the caller to fix the inputs.

## CI & Local Verification
- Formatted the codebase utilizing `sbt scalafmtAll` to ensure compliance with the project's style guide.
- Verified compilation and passing of all unit tests by running `sbt buildAll` locally.
- Confirmed zero compiler warnings related to ScalaDoc syntax.

## Self-Learnings / Environment Setup
- Successfully set up the local Windows environment by installing JDK (Eclipse Temurin 21) and the `sbt` build tool using `winget`.
- Addressed initial pathing/sub-process execution issues on Windows by ensuring shell execution constraints were properly configured.
- The `sbt buildAll` step successfully seeded local Coursier caches and built the entire project from scratch locally to guarantee CI readiness.
