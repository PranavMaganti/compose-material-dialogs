# Core

### Basic Dialogs

Here is an example to make a basic dialog with a title, text and buttons:

```kotlin
val dialog = MaterialDialog()

dialog.build {
    title("This is a simple dialog")
    message("""Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum
                    hendrerit risus eu sem aliquam rhoncus. Aliquam ullamcorper tincidunt elit,
                    in aliquam sapien. Nunc a porttitor nulla, at semper orci.""")
    positiveButton("Ok", onClick = { 
        //Do Something
    })
    negativeButton("Cancel", onClick = { 
        //Do Something
    })
}

dialog.show()
```

The dialog is shown when the `dialogShowing` state is set to true (eg. when a button press). 

The hard coded strings for all the components in the example above can be replaced with a string resource id. 

If the `autoDismiss` parameter of a `MaterialDialog`  is set to false then the dialog will not close when the positive or negative buttons are clicked, allowing flexibility with custom views.

### List Dialogs

Below is an example of a plain list dialog:

```kotlin
dialog.build {
    ...
    listItems(listOf("Item 1", "Item 2", "Item 3"), onClick { index, item ->
        //Do Something
    })
    ...
}
```



If you would like to use a custom list item layout you can use the following code:

```kotlin
data class TextIcon(text: String, icon: ImageAsset)
val items = listOf(
    TextIcon("Item 1", imageFromResource(R.drawable.item_one))
    TextIcon("Item 2", imageFromResource(R.drawable.item_two))
    TextIcon("Item 3", imageFromResource(R.drawable.item_three))
)

dialog.build {
    ...
    listItems(items, onClick { index, item ->
        //Do Something
    }) {
        Row {
            Box(Modifier.preferedSize(40.dp)) {
                Image(it.icon)
            }
            Text(it.text, modifier = Modifier.padding(start = 12.dp))
        }
    }
	...
}
```

### Single Choice List

```kotlin
dialog.build {
    listItemsSingleChoice(
        list = listOf("Item 1", "Item 2", "Item 3"),
        disabledIndices = listOf(1),
        initialSelection = 2
    ) {
        //Do Something
    }
}
```

As seen in the code snippet above you can pass in a list of indices of items which should be disabled (ie. cannot be selected) and the item index which should selected to start with. By default, if no initial selection is given it will default to the first item.

### Multiple Choice List

```kotlin
dialog.build {
    listItemsSingleChoice(
        list = listOf("Item 1", "Item 2", "Item 3"),
        disabledIndices = listOf(1),
        initialSelection = listOf(1)
    ) {
        //Do Something
    }
}
```

As seen in the code snippet above you can select which items will be disabled (ie. cannot be selected) and also a list of item indicies which should be initially selected.

### Custom View

```kotlin
dialog.build {
    customView {
    	//Define your view here
    }
}
```

