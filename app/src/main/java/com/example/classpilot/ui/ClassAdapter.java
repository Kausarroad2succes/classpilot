package com.example.classpilot.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.databinding.ItemClassBinding;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    public interface OnClassClickListener {
        void onClassClick(ClassEntity c);
    }

    private List<ClassEntity> items = new ArrayList<>();
    private final OnClassClickListener listener;

    public ClassAdapter(OnClassClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<ClassEntity> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClassBinding b = ItemClassBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ClassViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassEntity c = items.get(position);
        holder.binding.tvName.setText(c.name);
        String code = (c.courseCode == null || c.courseCode.isEmpty())
                ? "No course code" : c.courseCode;
        holder.binding.tvDetails.setText(code + "  |  " + c.totalTopics + " topics");
        holder.itemView.setOnClickListener(v -> listener.onClassClick(c));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        final ItemClassBinding binding;

        ClassViewHolder(ItemClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}