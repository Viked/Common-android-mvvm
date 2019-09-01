package com.viked.commonandroidmvvm.ui.dialog.purchase

import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.DialogPurchaseBinding
import com.viked.commonandroidmvvm.ui.adapters.list.DelegateRecyclerViewAdapter
import com.viked.commonandroidmvvm.ui.adapters.list.ListDelegate
import com.viked.commonandroidmvvm.ui.common.click.BaseClickComponent
import com.viked.commonandroidmvvm.ui.dialog.BaseDialogFragment


/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseDialogFragment : BaseDialogFragment<PurchaseViewModel, DialogPurchaseBinding>() {

    override val layoutId = R.layout.dialog_purchase

    override val viewModelClass = PurchaseViewModel::class.java

    override fun initView(binding: DialogPurchaseBinding, viewModel: PurchaseViewModel) {
        super.initView(binding, viewModel)
        val adapter = DelegateRecyclerViewAdapter()

        adapter.addDelegate(PurchaseDelegate(layoutInflater).apply {
            this.onItemClickListener = BaseClickComponent { i ->
                val model = this@PurchaseDialogFragment.viewModel.value
                val item = adapter.items.getOrNull(i) as? PurchaseItemWrapper

                if (model != null && item != null && item.purchase == null) {
                    model.startFlow(item)
                }
                true
            }
        })
        binding.rvPurchases.adapter = adapter
        viewModel.list.observe(this, ListDelegate(binding.tvStatus, adapter))
    }

}