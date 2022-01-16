# Contributing to Compose Material Dialogs

:confetti_ball: Firstly, thanks for taking time to contribute to the library!​ :confetti_ball: 

Below are all the details required to get started with the code base (including structure, testing and styling)

## Getting Started

### Codebase

The code base is split up into 3 main modules: `core` , `color` and `datetime`.  The core module contains the majority of the code to handle the creation of the base dialog and provides the basic dialog components such a titles, text and inputs. This module also handles the creation of buttons and their layout. The other 2 modules are quite self explanatory with the `color` module providing a color selection dialog and the `datetime` module providing separate date and time dialogs. 

### Styleguides

#### Git Commit Messages

The following are just suggestions but would be good to follow to keep the commit history consistent and readable:

- Use present tense ("Fix bug" not "Fixed bug")
- Use imperative mood ("Change variable name" not "Changes variable name")
- Limit the first line to 75 characters or less
- Make the commit message specific ("Refactor MaterialDialog class methods" not "Refactor classes")

#### Kotlin Styleguide

All the code is linted using Ktlint through the [spotless](https://github.com/diffplug/spotless) gradle plugin. In order to check if your code matches Ktlint's styling you can use the command:

```bash
./gradlew spotlessCheck
```

You can also fix the styling of your code using the following command:

```
./gradlew spotlessApply
```

NOTE: The `spotlessApply` task does not fix wildcard imports which might be automatically formatted by Android Studio. You can avoid this by 

changing Android Studio's import settings in `Editor -> Code Style -> Kotlin -> Imports tab` and then change it to `Use single name imports`.

### Testing

The project consists of 3 types of tests:

- Unit tests -  Used for any function which can be tested independently of UI 
- Functionality tests - Used to test functionality of UI
- Screenshot tests - Used to test the appearance of UI

The first 2 of these tests can be run on any type of device however the screenshot tests require you to use an emulator with a specific device and version so that they are consistent and can be compared.

You will need to create a new AVD with the following properties, this can be done either through Android Studio or command line:

```
Device: Nexus 6P
API Level: 28
```

To ensure consistant reporducable screenshot testing the emulator has to be started using the command line with some additional parameters (repace [AVD Name] with the one you created in the previous step):

```
emulator -avd [AVD NAME] -no-window -gpu swiftshader_indirect
```

To run the Unit and Functionality tests locally you can run the following command:

```
./gradlew connectedCheck -Pandroid.testInstrumentationRunnerArguments.filter=com.vanpra.composematerialdialogs.test.utils.NotScreenshotTestFilter
```

To run the screenshot tests you can use:

```
./gradlew executeScreenshotTests -Pandroid.testInstrumentationRunnerArguments.filter=com.vanpra.composematerialdialogs.test.utils.ScreenshotTestFilter
```

You can then view the screenshot test report at in the directory `[module]/build/screenshots/`

If you have added a new screenshot test or changed the UI such that it affects an existing screenshot test you will have to recapture the screenshots using an emulator. Before doing this run the screenshot tests to verify that all the tests which are not affected by the change are still passing. After doing so you can run the following command which will capture on the emulator which should be setup as mentioned above:

```
./gradlew executeScreenshotTests -Precord -Pandroid.testInstrumentationRunnerArguments.filter=com.vanpra.composematerialdialogs.test.utils.ScreenshotTestFilter
```

## Pull Requests

All PR's will have to pass the CI pipeline before they can be reviewed. The CI consists of a `lint and build` stage which makes sure the styling complies with Ktlint and the code compiles. After this the CI runs the `testing ` stage which will run the screenshot and functional tests mentioned above. 

When creating a pull request you will be given a template you can fill out and once the CI is passing someone will review your changes. 