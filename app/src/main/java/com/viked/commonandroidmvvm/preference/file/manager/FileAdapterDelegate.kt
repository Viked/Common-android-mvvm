package com.viked.commonandroidmvvm.preference.file.manager

import android.view.LayoutInflater
import com.android.databinding.library.baseAdapters.BR
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterDelegate


/**
 * Created by Viked on 12/31/2016.
 */
class FileAdapterDelegate(inflater: LayoutInflater) : AdapterDelegate(FileItemWrapper::class, inflater, R.layout.item_file, BR.viewModel)