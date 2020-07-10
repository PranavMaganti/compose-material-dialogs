# Compose Material Dialogs

**Current Compose Version: dev14**

## Credits

This library's design is heavily inspired by https://github.com/afollestad/material-dialogs

## Download

## Core

### Usage

[ ![Download](https://api.bintray.com/packages/vanpra/maven/compose-material-dialogs:core/images/download.svg?version=0.1.0) ](https://bintray.com/vanpra/maven/compose-material-dialogs:core/0.1.0/link)

```gradle
dependencies {
  ...
  implementation 'com.vanpra.compose-material-dialogs:core:0.1.0'
  ...
}
```

### Basic Dialogs

Here is an example to make a basic dialog with a title, text and buttons:

```kotlin
val dialogShowing = state { false }

MaterialDialog(dialogShowing, autoDismiss = false).draw {
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
```

The dialog is shown when the `dialogShowing` state is set to true (eg. when a button press). 

The hard coded strings for all the components in the example above can be replaced with a string resource id. 

If the `autoDismiss` parameter of a `MaterialDialog`  is set to false then the dialog will not close when the positive or negative buttons are clicked, allowing flexibility with custom views.

There is also an option to pass a drawable resource id to the the title through the parameter `icon` which will display the drawable to the left of the title.

### List Dialogs

Below is an example of a plain list dialog:

```kotlin
MaterialDialog(dialogShowing).draw {
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

MaterialDialog(dialogShowing).draw {
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
MaterialDialog(dialogShowing).draw {
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
MaterialDialog(dialogShowing).draw {
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
MaterialDialog(dialogShowing).draw {
    customView {
    	//Define your view here
    }
}
```



## To do

1. Merge/refactor date picker dialog into this repository
2. Add missing dialogs such as colour picker and file chooser
3. Improve documentation to be more informative and add images
4. Add comments to all `MaterialDialog` functions