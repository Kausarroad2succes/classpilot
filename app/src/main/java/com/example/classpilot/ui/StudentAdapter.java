package com.example.classpilot.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;
import com.example.classpilot.databinding.ItemStudentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface Listener {
        void onStudentClick(Student s);
        boolean isPresent(Student s);
    }

    private static final double REQUIRED_PERCENT = 75.0;
    private static final int RED = Color.parseColor("#C62828");
    private static final int GREY = Color.parseColor("#616161");
    private static final int BG_PRESENT = Color.parseColor("#E8F5E9");
    private static final int BG_ABSENT = Color.parseColor("#FFEBEE");

    private List<Student> students = new ArrayList<>();
    private Map<String, StudentPercentage> percentages = new HashMap<>();
    private final Listener listener;

    public StudentAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setStudents(List<Student> list) {
        students = list;
        notifyDataSetChanged();
    }

    public void setPercentages(Map<String, StudentPercentage> p) {
        percentages = p;
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentBinding b = ItemStudentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student s = students.get(position);
        ItemStudentBinding b = holder.binding;

        b.tvRoll.setText((s.roll == null || s.roll.isEmpty()) ? "-" : s.roll);

        boolean hasName = s.name != null && !s.name.isEmpty();
        b.tvName.setVisibility(hasName ? View.VISIBLE : View.GONE);
        if (hasName) b.tvName.setText(s.name);

        boolean present = listener.isPresent(s);
        b.cbPresent.setChecked(present);
        b.cardRoot.setCardBackgroundColor(present ? BG_PRESENT : BG_ABSENT);

        StudentPercentage p = percentages.get(s.id);
        if (p == null) {
            b.tvPercent.setText("No record yet");
            b.tvPercent.setTextColor(GREY);
        } else {
            boolean low = p.percentage < REQUIRED_PERCENT;
            String text = String.format(Locale.getDefault(), "%.1f%% (%d/%d)",
                    p.percentage, p.presentCount, p.totalCount);
            if (low) text += "  - BELOW 75%";
            b.tvPercent.setText(text);
            b.tvPercent.setTextColor(low ? RED : GREY);
        }

        holder.itemView.setOnClickListener(v -> listener.onStudentClick(s));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        final ItemStudentBinding binding;

        StudentViewHolder(ItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}