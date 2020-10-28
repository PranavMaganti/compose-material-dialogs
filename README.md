# Compose Material Dialogs

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5990ad24f5ca434299916697e3fc0fe2)](https://app.codacy.com/manual/pranav.maganti/compose-material-dialogs?utm_source=github.com&utm_medium=referral&utm_content=vanpra/compose-material-dialogs&utm_campaign=Badge_Grade_Dashboard)

**Current Compose Version: 1.0.0-alpha06**

## Credits

This library's design is heavily inspired by https://github.com/afollestad/material-dialogs

## Core

#### [Core Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/Core.md)

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/full_core.png)

[ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs%3Acore/images/download.svg) ](https://bintray.com/vanpra/maven/compose-material-dialogs%3Acore/_latestVersion)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:core:0.2.5" 
  ...
}
```

## Date and Time Picker

#### [Date and Time Picker Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/DateTimePicker.md)

![](https://raw.githubusercontent.com/vanpra/ComposeDateTimePicker/master/imgs/datetime.jpg)

 [ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs%3Adatetime/images/download.svg) ](https://bintray.com/vanpra/maven/compose-material-dialogs%3Adatetime/_latestVersion)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:datetime:0.2.5"
  ...
}
```

## Color Picker

#### [Color Picker Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/ColorPicker.md)

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/color_picker.png)

 [ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs%3Acolor/images/download.svg) ](https://bintray.com/vanpra/maven/compose-material-dialogs%3Acolor/_latestVersion)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:color:0.2.5"
  ...
}
```



## To do

1. [WIP] Add year picker to date dialog
2. Add missing file/folder picker dialog
3. Add tests to all modules

## Changelog

### 0.2.5  - 2020-10-28

- Updated compose version to 1.0.0-alpha06

### 0.2.4  - 2020-10-15

- Updated compose version to 1.0.0-alpha05
- Fixed crash when dialog button values such as text are changed
- Clean up gradle files

### 0.2.3  - 2020-10-02

- Updated compose version to 1.0.0-alpha04
- Add optimisations to date picker 
- Fix ARGB color picker layout

### 0.2.2  - 2020-09-16

- Updated compose version to 1.0.0-alpha03

### 0.2.1  - 2020-09-02

- Updated compose version to 1.0.0-alpha02

### 0.2.0  - 2020-08-26

- Updated compose version to 1.0.0-alpha01

### 0.1.8  - 2020-08-19

- Updated compose version to dev17
- Updated kotlin version to 1.4.0
- Fix date dialog and time dialog to also fit on smaller screens

### 0.1.6  - 2020-08-05

- Updated compose version to dev16
- Updated kotlin version to 1.4.0-rc
- Fix datetime dialog to fit on smaller screens

### 0.1.5  - 2020-07-23

- Updated compose version to dev15
- Updated kotlin version to 1.4-M3
- Make dialog button text uppercase

