## What does this PR do?
This PR introduces comprehensive unit tests for the `SqlIdentifier` utility via a new `SqlIdentifierSpec.scala` test file.
The tests cover safety-critical validation logic to ensure PostgreSQL identifiers are safely sanitized, which mitigates potential SQL injection risks. Included test cases cover:
- Valid simple names and identifiers with underscores.
- Invalid names starting with a digit.
- Disallowed hyphens.
- SQL injection payload attempts (e.g. `DROP TABLE`).
- Empty and null values handling.
- Maximum identifier length boundary violations (PostgreSQL 63-character limit).
- Mixed case unquoted strings.

## Related issue
Fixes #945

## How was this tested?
- Ran `sbt "core/testOnly *SqlIdentifierSpec"` locally to confirm the behavior of the `SqlIdentifier` tests specifically.
- Ran `sbt scalafmtAll` to ensure style conformance.
- Validated with `llm4s-pr-manager` verifying overall pipeline integrity (`sbt buildAll`), confirming that these tests do not break any builds and pass across Scala versions.

## Checklist

- [x] I have read the [Contributing Guide](https://llm4s.org/reference/contributing)
- [x] PR is small and focused — one change, one reason
- [x] `sbt scalafmtAll` — code is formatted
- [x] `sbt test` — tests pass on Scala 3
- [x] New code includes tests
- [x] No unrelated changes included (branched from `main`, not from another PR)
- [x] Commit messages explain the "why"
