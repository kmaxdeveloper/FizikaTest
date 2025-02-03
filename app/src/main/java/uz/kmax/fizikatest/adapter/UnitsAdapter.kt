package uz.kmax.fizikatest.adapter

import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.fizikatest.data.main.UnitsData
import uz.kmax.fizikatest.databinding.ItemDayHistoryBinding

class UnitsAdapter : BaseRecycleViewDU<ItemDayHistoryBinding, UnitsData>(ItemDayHistoryBinding::inflate) {

    override fun bind(binding: ItemDayHistoryBinding, item: UnitsData) {
        binding.storyOfDay.text = item.story
    }

    override fun areContentsTheSame(oldItem: UnitsData, newItem: UnitsData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: UnitsData, newItem: UnitsData) = oldItem.story == newItem.story

}