package com.example.classpilot.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classpilot.databinding.ItemReportBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private static final double REQUIRED_PERCENT = 75.0;
    private static final int RED = Color.parseColor("#C62828");
    private static final int GREEN = Color.parseColor("#2E7D32");
    private static final int GREY = Color.parseColor("#757575");

    private List<ReportRow> rows = new ArrayList<>();

    public void setRows(List<ReportRow> list) {
        rows = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportBinding b = ItemReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReportViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportRow r = rows.get(position);
        ItemReportBinding b = holder.binding;

        String roll = (r.roll == null || r.roll.isEmpty()) ? "?" : r.roll;
        b.tvRoll.setText("Roll " + roll);

        String namePart = (r.name == null || r.name.isEmpty()) ? "" : r.name + "  |  ";

        if (r.hasRecord) {
            boolean low = r.percentage < REQUIRED_PERCENT;
            int color = low ? RED : GREEN;
            b.progressBar.setProgress((int) Math.round(r.percentage));
            b.progressBar.setProgressTintList(ColorStateList.valueOf(color));
            b.tvPercent.setText(String.format(Locale.getDefault(), "%.1f%%", r.percentage));
            b.tvPercent.setTextColor(color);
            b.tvDetail.setText(namePart + r.presentCount + " of " + r.totalCount + " classes");
            b.tvDetail.setTextColor(low ? RED : GREY);
        } else {
            b.progressBar.setProgress(0);
            b.progressBar.setProgressTintList(ColorStateList.valueOf(GREY));
            b.tvPercent.setText("-");
            b.tvPercent.setTextColor(GREY);
            b.tvDetail.setText(namePart + "No record yet");
            b.tvDetail.setTextColor(GREY);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        final ItemReportBinding binding;

        ReportViewHolder(ItemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}