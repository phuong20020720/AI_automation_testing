# Contributing

## Workflow
1. Create a branch from main.
2. Make your changes and test locally.
3. Run the relevant checks before committing.
4. Commit with clear messages.
5. Open a pull request for review.

## Code style
- Keep Java code readable and consistent.
- Avoid hardcoding secrets or credentials.
- Prefer configuration via properties or environment variables.
- Keep test names descriptive and meaningful.

## Local configuration
- Copy .env.example to .env and update values before running tests.
- Do not commit secrets, tokens, or local-only credentials.

## Pull request checklist
- [ ] Code compiles locally
- [ ] Relevant tests were run
- [ ] Documentation was updated if needed
- [ ] No secrets were committed
