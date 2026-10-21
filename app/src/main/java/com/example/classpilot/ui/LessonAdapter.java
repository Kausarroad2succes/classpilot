package com.example.classpilot.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classpilot.data.entity.LessonLog;
import com.example.classpilot.databinding.ItemLessonBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<LessonLog> items = new ArrayList<>();
    private final SimpleDateFormat format =
            new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

    public void submit(List<LessonLog> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLessonBinding b = ItemLessonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LessonViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        LessonLog l = items.get(position);
        holder.binding.tvLessonDate.setText(format.format(new Date(l.date)));
        holder.binding.tvLessonTopic.setText(l.topic);

        if (l.notes == null || l.notes.trim().isEmpty()) {
            holder.binding.tvLessonNote.setVisibility(View.GONE);
        } else {
            holder.binding.tvLessonNote.setVisibility(View.VISIBLE);
            holder.binding.tvLessonNote.setText(l.notes);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        final ItemLessonBinding binding;

        LessonViewHolder(ItemLessonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}