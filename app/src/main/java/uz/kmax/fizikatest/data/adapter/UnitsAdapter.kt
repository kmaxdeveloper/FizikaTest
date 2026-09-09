package uz.kmax.fizikatest.data.adapter

import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.fizikatest.domain.models.UnitsData
import uz.kmax.fizikatest.databinding.ItemUnitsBinding

class UnitsAdapter : BaseRecycleViewDU<ItemUnitsBinding, UnitsData>(ItemUnitsBinding::inflate) {

    override fun bind(binding: ItemUnitsBinding, item: UnitsData) {
        binding.storyOfDay.text = item.unit
    }

    override fun areContentsTheSame(oldItem: UnitsData, newItem: UnitsData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: UnitsData, newItem: UnitsData) = oldItem.unit == newItem.unit

}