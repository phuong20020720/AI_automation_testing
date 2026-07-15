# AI Automation Testing

This repository contains a Java/Maven automation framework for web and mobile testing using Selenium, Appium, TestNG, and Allure.

## What is included
- Web automation with Selenium
- Mobile automation with Appium
- TestNG suite definitions under testng/
- Allure reporting integration
- Maven profiles for web and mobile execution

## Prerequisites
- JDK 17 or newer
- Maven 3.9+
- Chrome/Edge browser for web tests
- Appium and an emulator/device for mobile tests

## Install on macOS
If Homebrew is available, you can install the required tools with:

```bash
brew install openjdk@17 maven
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Run the tests
Run the web suite:

```bash
mvn test -P web
```

Run the mobile suite:

```bash
mvn test -P mobile
```

Generate the Allure report locally:

```bash
mvn allure:serve
```

## Project structure
- src/main: shared framework components, utilities, page object/model code
- src/test: test classes
- testng: TestNG XML suite files
- docs: additional documentation
- ai: prompts, rules, skills, and workflows

## CI
A basic GitHub Actions workflow is included to compile the project on push and pull requests.
