package com.example.classpilot.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classpilot.data.entity.SlotWithClass;
import com.example.classpilot.databinding.ItemSlotBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    public interface Listener {
        void onSlotClick(SlotWithClass item);
    }

    private static final String[] DAY_NAMES = {
            "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private List<SlotWithClass> items = new ArrayList<>();
    private final Listener listener;

    public SlotAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<SlotWithClass> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    /** Calendar day constant (Sunday = 1 ... Saturday = 7) to a name. */
    public static String dayName(int calendarDay) {
        if (calendarDay < 1 || calendarDay > 7) return "?";
        return DAY_NAMES[calendarDay];
    }

    /** Minutes since midnight to text, e.g. 570 -> "9:30 AM". */
    public static String formatTime(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        String ampm = h >= 12 ? "PM" : "AM";
        int h12 = h % 12;
        if (h12 == 0) h12 = 12;
        return String.format(Locale.US, "%d:%02d %s", h12, m, ampm);
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlotBinding b = ItemSlotBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SlotViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        SlotWithClass item = items.get(position);
        holder.binding.tvDayTime.setText(
                dayName(item.slot.dayOfWeek) + ", " + formatTime(item.slot.startMinutes));
        holder.binding.tvSlotClass.setText(item.className);
        holder.binding.tvSlotRoom.setText("Room: " + item.slot.room);
        holder.itemView.setOnClickListener(v -> listener.onSlotClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        final ItemSlotBinding binding;

        SlotViewHolder(ItemSlotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}