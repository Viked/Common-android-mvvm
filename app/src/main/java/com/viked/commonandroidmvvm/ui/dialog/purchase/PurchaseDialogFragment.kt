package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.view.ViewGroup
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.DialogPurchaseBinding
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterBehavior
import com.viked.commonandroidmvvm.ui.adapters.list.DelegateRecyclerViewAdapter
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.click.BaseClickComponent
import com.viked.commonandroidmvvm.ui.dialog.BaseDialogFragment


/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseDialogFragment : BaseDialogFragment<PurchaseViewModel, DialogPurchaseBinding>() {

    private lateinit var adapter: AutoClearedValue<DelegateRecyclerViewAdapter>

    private lateinit var behavior: AutoClearedValue<AdapterBehavior>

    override val layoutId = R.layout.dialog_purchase

    override val viewModelClass = PurchaseViewModel::class.java

    override fun setViewModelToBinding(binding: DialogPurchaseBinding, viewModel: PurchaseViewModel) {
        binding.viewModel = viewModel
    }

    override fun initView(binding: DialogPurchaseBinding, viewModel: PurchaseViewModel) {
        super.initView(binding, viewModel)
        val adapter = DelegateRecyclerViewAdapter(viewModel.list)
        val behavior = AdapterBehavior(adapter, viewModel.list)
        this.behavior = AutoClearedValue(this, behavior)
        addAdapterDelegate(behavior)
        this.adapter = AutoClearedValue(this, adapter)

        adapter.addDelegate(PurchaseDelegate(layoutInflater).apply {
            this.onItemClickListener = BaseClickComponent { i, v ->
                val model = this@PurchaseDialogFragment.viewModel.value
                if (model != null && i is PurchaseItemWrapper && i.purchase == null) {
                    model.startFlow(i.billingItem.sku)
                }
                true
            }
        })
        binding.rvPurchases.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onResume() {
        super.onResume()
        viewModel.value?.onRefresh()
    }

}