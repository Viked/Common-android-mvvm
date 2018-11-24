package com.viked.commonandroidmvvm.preference.file.manager

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.DialogFileManagerPreferenceBinding
import com.viked.commonandroidmvvm.ui.adapters.list.DelegateRecyclerViewAdapter
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ListDelegate
import com.viked.commonandroidmvvm.ui.binding.addOnPropertyChangeListener
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.click.ClickDecorator
import com.viked.commonandroidmvvm.ui.data.Resource
import java.io.File

/**
 * Created by 1 on 20.03.2016.
 */
class FileManagerPreferenceDialogFragment : PreferenceDialogFragmentCompat() {

    private lateinit var binding: AutoClearedValue<DialogFileManagerPreferenceBinding>
    private val files = MutableLiveData<Resource<List<ItemWrapper>>>()

    val path = ObservableField<File>(File(ROOT_DIR)).apply {
        addOnPropertyChangeListener {
            binding.value?.tvSelectedFolder?.text = it.get()?.absolutePath
            loadFileList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            path.set(File(this.getFileManagerPreference().path))
        } else {
            path.set(File(savedInstanceState.getString(SELECTED_PATH_KEY)))
        }
    }

    private fun getFileManagerPreference(): FileManagerPreference {
        return this.preference as FileManagerPreference
    }

    override fun onCreateDialogView(context: Context?): View {
        val dataBinding = DataBindingUtil
                .inflate<DialogFileManagerPreferenceBinding>(layoutInflater, R.layout.dialog_file_manager_preference, null, false)
        binding = AutoClearedValue(this, dataBinding)

        dataBinding.ibCreateFolder.setOnClickListener { view ->
            NewFolderDialogFragment.newInstance(this, CREATE_DIR_CODE,
                    getString(R.string.create_folder_title), getString(R.string.create_folder_msg), "")
                    .show(fragmentManager, "createDirDialog")
        }
        dataBinding.fileListView.layoutManager = LinearLayoutManager(context)

        val adapter = DelegateRecyclerViewAdapter()
        adapter.addDelegate(FileAdapterDelegate(layoutInflater).apply {
            onItemClickListener = ClickDecorator(onItemClickListener) { v, i ->
                val item = adapter.items[i]
                if (item.value is File) {
                    path.set(if (item.value.name != DIR_UP_FILE_NAME) {
                        item.value
                    } else {
                        item.value.parentFile.parentFile
                    })
                    true
                } else {
                    false
                }
            }
        })
        dataBinding.fileListView.adapter = adapter
        files.observe(this, ListDelegate(dataBinding.tvStatus, adapter))
        return dataBinding.root
    }

    private fun showError(errorStringResource: Int) {
        Snackbar.make(binding.value?.root!!, errorStringResource, Snackbar.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_PATH_KEY, path.get()?.absolutePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_DIR_CODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                createFolder(data.getStringExtra(NewFolderDialogFragment.EDIT_VALUE_KEY))
            }
        }
    }

    override fun onDialogClosed(p0: Boolean) {
        val preference = getFileManagerPreference()
        if (p0) {
            val value = path.get()?.absolutePath ?: ""
            if (preference.callChangeListener(value)) {
                preference.path = value
            }
        }
    }

    private fun createFolder(folderName: String) {
        val newFolder = File(path.get(), folderName)
        when {
            !newFolder.canWrite() -> showError(R.string.no_write_access)
            newFolder.exists() -> showError(R.string.error_already_exists)
            !newFolder.mkdir() -> showError(R.string.create_folder_error)
            else -> path.set(newFolder)
        }
    }

    private fun loadFileList() {
        val path = path.get()
        val list = mutableListOf<File>()
        list.addAll(path?.listFiles() ?: emptyArray())
        if ((path?.absolutePath == ROOT_DIR).not()) {
            list.add(File(path, DIR_UP_FILE_NAME))
        }
        list.sortWith(Comparator { file1, file2 ->
            when {
                file1.isDirectory && file2.isFile -> -1
                file1.isFile && file2.isDirectory -> 1
                else -> file1.path.compareTo(file2.path, ignoreCase = true)
            }
        })

        files.postValue(Resource.success(list.map { FileItemWrapper(it) }))
    }

}