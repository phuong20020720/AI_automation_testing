# AI Automation Testing

This repository contains a Java/Maven automation framework for web and mobile testing using Selenium, Appium, TestNG, and Allure.

## Overview
This project is designed to support automated regression and smoke testing for both web and mobile applications. It includes reusable framework components, test suites, and AI-assisted workflow assets.

## Key features
- Web automation with Selenium
- Mobile automation with Appium
- TestNG suite definitions under testng/
- Allure reporting integration
- Maven profiles for web and mobile execution
- AI prompts, rules, skills, and workflows under ai/

## Prerequisites
- JDK 17 or newer
- Maven 3.9+
- Chrome/Edge browser for web tests
- Appium and an emulator/device for mobile tests

## Step-by-step setup on macOS

### 1. Install Homebrew (if not already installed)

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

If needed, add Homebrew to your shell profile:

```bash
echo 'eval "$$(/opt/homebrew/bin/brew shell_env)"' >> ~/.zshrc
source ~/.zshrc
```

### 2. Install Java and Maven

```bash
brew install --cask temurin@17
brew install maven
```

Set Java home and make sure Maven uses it:

```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Verify:

```bash
java -version
mvn -v
```

### 3. Install Chrome for Selenium web tests

```bash
brew install --cask google-chrome
```

### 4. Install Node.js and Appium for mobile tests

```bash
brew install node
npm install -g appium
appium driver install uiautomator2
```

Verify:

```bash
node -v
appium -v
appium driver list --installed
```

### 5. Install Android Studio and create an emulator

```bash
brew install --cask android-studio
```

Then open Android Studio and:
- Open Android Studio
- Go to More Actions → SDK Manager
- Install Android SDK Platform 33 and Android Emulator
- Open Device Manager and create an emulator such as Pixel 7 API 33

Verify the emulator is visible:

```bash
adb devices
```

### 6. Start the required services before mobile tests

Open two terminals:

Terminal 1 — start the emulator:

```bash
emulator -avd <your_avd_name>
```

Terminal 2 — start Appium:

```bash
appium
```

### 7. Run the tests

Web suite:

```bash
mvn test -P web
```

Mobile suite:

```bash
mvn test -P mobile
```

## Run tests
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
- docs: documentation and implementation notes
- ai: prompts, rules, skills, and workflows
- config: environment and framework configuration

## CI
A basic GitHub Actions workflow is included to compile the project on push and pull requests.

## Contribution
Please read [CONTRIBUTING.md](CONTRIBUTING.md) before making changes.
