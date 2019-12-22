package com.viked.commonandroidmvvm.ui.adapters.list

import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import com.viked.commonandroidmvvm.BR
import com.viked.commonandroidmvvm.R
import java.io.File


/**
 * Created by Viked on 12/31/2016.
 */

const val DIR_UP_FILE_NAME = ".."
const val ROOT_DIR = "/"

class FileItemWrapper(val file: File) : ItemWrapper(file.absolutePath.hashCode().toLong(), file) {

    override fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        val item = oldItem as? FileItemWrapper ?: return false
        return file.absolutePath == item.file.absolutePath
    }

    val icon = when {
        file.name == DIR_UP_FILE_NAME -> R.drawable.folder_outline
        file.isDirectory -> R.drawable.folder
        else -> R.drawable.file
    }

}

class FileAdapterDelegate(inflater: LayoutInflater) : AdapterDelegate(FileItemWrapper::class, inflater, R.layout.item_file, BR.viewModel)

object FileNameInputFilter : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int) =
            if (source.all { it.isLetterOrDigit() || it.isWhitespace() }) null else ""

}


object FileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return when {
            file1.isDirectory && file2.isFile -> -1
            file1.isFile && file2.isDirectory -> 1
            else -> file1.path.compareTo(file2.path, ignoreCase = true)
        }
    }
}


