package com.viked.commonandroidmvvm.ui.adapters.list.radio

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.BR
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterDelegate

class RadioAdapterDelegate(inflater: LayoutInflater) : AdapterDelegate(RadioItemWrapper::class, inflater, R.layout.item_radio, BR.viewModel, BR.delegate)