package com.viked.commonandroidmvvm.preference.file.manager

import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import java.io.File

class FileItemWrapper(val file: File) : ItemWrapper(file) {

    val icon = when {
        file.name.equals(DIR_UP_FILE_NAME) -> R.drawable.folder_outline
        file.isDirectory -> R.drawable.folder
        else -> R.drawable.file
    }

}