# Date Time Picker

### Date Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/date.jpg)

```kotlin
val dialog = MaterialDialog()
dialog.build {
    ...
    datepicker { date ->
        // Do stuff with java.time.LocalDate object which is passed in
    }
}

dialog.show()
```

### Time Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/time.jpg)

```kotlin
dialog.build {
    ...
    timepicker { time ->
        // Do stuff with java.time.LocalTime object which is passed in
    }
    ...
}
```



### Date and Time Picker

![](https://raw.githubusercontent.com/vanpra/compose-material-dialogs/main/imgs/datetime.jpg)

```kotlin
dialog.build {
    datetimepicker { dateTime ->
        // Do stuff with java.time.LocalDateTime object which is passed in
    }
}
```

Unlike the other two dialogs the date and time picker has a title build-in and also had a title parameter which can be used set the title. This is needed as the date time picker uses the title space to show the back button.

### Change initial date

To use the date last inputted by the user as the starting point you can make use of the `initialDateTime` parameter along with a mutable state object to keep track of the last selected date. Here is an example of such usage with the DateTime picker.

```kotlin
val selectedDateTime = state { LocalDateTime.now() }

dialog.build {
    datetimepicker(initalDateTime = selectedDateTime.value) { dateTime ->
        selectedDateTime.value = dateTime
        ...
    }
}
```

## To Do

1. Limit date selection range (ie. min/max date)

2. Implement Date range selection 

3. Implement year selection

   
