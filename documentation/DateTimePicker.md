# Date Time Picker

### Date and Time Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/datetime.jpg)

```kotlin
dialog.build {
    datetimepicker { dateTime ->
        // Do stuff with java.time.LocalDateTime object which is passed in
    }
}
```

To show the dialog just call `dialog.show()`

### Date Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/date.jpg)

```kotlin
dialog.build {
    datepicker { date ->
        // Do stuff with java.time.LocalDate object which is passed in
    }
}
```

To show the dialog just call`dialog.show()`

### Time Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/time.jpg)

```kotlin
dialog.build {
    timepicker { time ->
        // Do stuff with java.time.LocalTime object which is passed in
    }
}
```

To show the dialog just call `dialog.show()`

### Change initial date

To use the date last inputted by the user as the starting point you can make use of the `initialDateTime` parameter along with a mutable state object to keep track of the last selected date. Here is an example of such usage with the DateTime picker.

```kotlin
val selectedDateTime = state { LocalDateTime.now() }

dialog.build {
    datetimepicker(selectedDateTime.value) { dateTime ->
        selectedDateTime.value = dateTime
        ...
    }
}
```

## To Do

1. Limit date selection range (ie. min/max date)

2. Implement Date range selection 

3. Implement year selection

   
