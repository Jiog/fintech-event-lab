# Fintech Event Lab — learning agreements

## Purpose and learner
- This is a personal learning project for an experienced Java/Spring fintech Senior Engineer / Technical Lead.
- Assume practical knowledge of payments, PSP integrations, swaps, SQL, RabbitMQ/JMS and production troubleshooting.
- Focus on Kafka, distributed consistency/recovery, modern Java performance and observability. Explain unfamiliar mechanisms and trade-offs, not elementary Java.
- Discuss in Spanish. Use English for code, identifiers, commits and the public README. Learning notes may be Spanish.

## Scope and workflow
- Work on one small lesson at a time. Read `docs/learning-plan.md` to identify the current lesson.
- Explain the intended behavior and material trade-offs before implementing a design still under discussion; wait for explicit agreement on those choices.
- Make changes only when requested. Treat reviews, discussions and proposals as read-only.
- Keep changes reviewable. Do not advance to the next lesson automatically.
- End an implementation with a short reading order, evidence of validation and one concrete question or exercise.
- Distinguish implemented, compiled, executed and verified behavior. Never imply unrun tests passed.
- Do not run tests or failure/load experiments unless explicitly requested. Defer tests until implementation is complete and the design is agreed.
- Fetching refs for reviews is allowed; do not checkout, stash, merge or rebase for a review.
- Do not commit, push, publish or create remote resources unless requested.

## Engineering
- Use the latest stable direct dependencies and runtimes, checking official release sources and compatibility at adoption time. Pin concrete versions; avoid floating `latest` tags.
- Keep Spring dependencies aligned through the latest stable Spring Boot BOM. Explain any compatibility constraint instead of silently downgrading or overriding it.
- Prefer simple code and SQL. Add infrastructure and abstractions when the current exercise needs them.
- Preserve unrelated behavior. Read only relevant files.
- Use synthetic scenarios and original code. Work repositories are learning context, not source material to copy into this public project.
- Bind the local simulation to loopback. It has no production authentication or money movement.
