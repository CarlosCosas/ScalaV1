# Repository Guidelines

## Project Structure & Module Organization
- Root: `build.sbt`, `README.md`, `LICENSE`, `project/` (SBT meta: `build.properties`, `plugins.sbt`).
- Source: `src/main/scala` under package `org.carlos.app` (e.g., `main.scala`, `SimpleMcpClient.scala`, `JenksScala.scala`).
- Data/assets: a CSV currently lives at `src/main/scala/stockPVN.csv`. Prefer `src/main/resources` for runtime assets.
- Tests: expected in `src/test/scala` (none committed yet).

## Build, Test, and Development Commands
- `sbt clean compile` — resolve dependencies and compile (Scala 3.7.3).
- `sbt run` — run the app; if multiple mains exist, SBT may prompt.
- `sbt "runMain org.carlos.app.SimpleMcpClientApp"` — run the MCP client example.
- `sbt console` — Scala REPL with project classpath.
- `sbt update` — refresh dependency resolution.

## Coding Style & Naming Conventions
- Scala 3 style, 2‑space indentation, 100–120 column width.
- Naming: `PascalCase` for classes/objects; `camelCase` for methods/vals; files match the primary `object`/`class`.
- Package: keep code under `org.carlos.app` and group by feature.
- Imports: prefer explicit imports over wide `_*` when practical.
- Formatting: no formatter configured; if added, use Scalafmt (`sbt scalafmtAll`).

## Testing Guidelines
- Framework: not configured. Prefer ScalaTest or MUnit in `Test` scope.
- Layout: mirror source packages under `src/test/scala`.
- Naming: `<Name>Spec.scala` per unit (e.g., `JenksScalaSpec.scala`).
- Run: `sbt test`. Add scoverage if coverage is required.

## Commit & Pull Request Guidelines
- Commits: concise, present‑tense. Prefer Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`).
- PRs: include summary, motivation, key changes, run instructions, and any screenshots/logs. Link issues and note breaking changes.
- Scope: keep PRs focused; add tests for new logic when applicable.

## Security & Configuration Tips
- Do not commit secrets or local paths; use env vars or config files ignored by VCS.
- Place runtime data in `src/main/resources` and load via classpath instead of absolute paths.
- MCP examples require external tools (Node/Python); keep them commented in code unless documented.

