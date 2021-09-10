# Date Time Picker

## Prerequisite

The date time picker relies on parts of the`java.time` API  which are only available on Android API levels >= 26. Therefore, in order to make this library backwards comparability with older Android API a few options have to be set in your app/module `build.gradle` file which enables desugaring:

````gradle
android {
	...
    compileOptions {
      // Flag to enable support for the new language APIs
      coreLibraryDesugaringEnabled true
        
      // Sets Java compatibility to Java 8
      sourceCompatibility JavaVersion.VERSION_1_8
      targetCompatibility JavaVersion.VERSION_1_8
	}
	...
}

dependencies {
  ...
  coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.0.9'
}
````

Note, this only has to be done if you intend to target an Android API level < 26. To find out more about desugaring you can check out: https://developer.android.com/studio/write/java8-support#library-desugaring. 

## Documentation

### Date Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/date.png)

```kotlin
val dialogState = rememberMaterialDialogState()
MaterialDialog(
    dialogState = dialogState,
    buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }
) {
    ...
    datepicker { date ->
        // Do stuff with java.time.LocalDate object which is passed in
    }
}

/* This should be called in an onClick or an Effect */ 
dialog.show()
```

### Time Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/time.png)

```kotlin
val dialogState = rememberMaterialDialogState()
MaterialDialog(
    dialogState = dialogState,
    buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }
) {
    ...
    timepicker { time ->
        // Do stuff with java.time.LocalTime object which is passed in
    }
    ...
}
```

## To Do

1. Limit date selection range (ie. min/max date)

2. Implement Date range selection 
