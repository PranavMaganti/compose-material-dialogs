# Compose Material Dialogs

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5990ad24f5ca434299916697e3fc0fe2)](https://app.codacy.com/manual/pranav.maganti/compose-material-dialogs?utm_source=github.com&utm_medium=referral&utm_content=vanpra/compose-material-dialogs&utm_campaign=Badge_Grade_Dashboard)

**Current Compose Version: dev16**

## Credits

This library's design is heavily inspired by https://github.com/afollestad/material-dialogs

## Core

#### [Core Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/Core.md)

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/full_core.png)

[ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs:core/images/download.svg?version=0.1.6) ](https://bintray.com/vanpra/maven/compose-material-dialogs:core/0.1.6/link)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:core:0.1.6" 
  ...
}
```

## Date and Time Picker

#### [Date and Time Picker Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/DateTimePicker.md)

![](https://raw.githubusercontent.com/vanpra/ComposeDateTimePicker/master/imgs/datetime.jpg)

[ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs:datetime/images/download.svg?version=0.1.6) ](https://bintray.com/vanpra/maven/compose-material-dialogs:datetime/0.1.6/link)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:datetime:0.1.6"
  ...
}
```

## Color Picker

#### [Color Picker Documentation](https://github.com/vanpra/compose-material-dialogs/blob/main/documentation/ColorPicker.md)

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/color_picker.png)

[ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs:color/images/download.svg?version=0.1.6) ](https://bintray.com/vanpra/maven/compose-material-dialogs:color/0.1.6/link)

```gradle
dependencies {
  ...
  implementation "com.vanpra.compose-material-dialogs:color:0.1.6"
  ...
}
```



## To do

1. Add missing file/folder picker dialog
2. Add tests to all modules

## Changelog

### 0.1.5  - 2020-07-23

- Updated compose version to dev15
- Updated kotlin version to 1.4-M3
- Make dialog button text uppercase

### 0.1.6  - 2020-08-05

- Updated compose version to dev16
- Updated kotlin version to 1.4.0-rc
- Fix datetime dialog to fit on smaller screens