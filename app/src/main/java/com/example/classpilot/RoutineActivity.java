package com.example.classpilot;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.data.entity.ScheduleSlot;
import com.example.classpilot.data.entity.SlotWithClass;
import com.example.classpilot.databinding.ActivityRoutineBinding;
import com.example.classpilot.databinding.DialogSlotBinding;
import com.example.classpilot.ui.SlotAdapter;
import com.example.classpilot.viewmodel.RoutineViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class RoutineActivity extends AppCompatActivity {
    private static final String TAG = "RoutineActivity";

    // Week order shown in the day spinner (Saturday first). Values are Calendar constants.
    private static final int[] DAY_VALUES = {
            Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY
    };

    private ActivityRoutineBinding binding;
    private RoutineViewModel viewModel;
    private SlotAdapter adapter;
    private List<ClassEntity> classes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Class routine");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(RoutineViewModel.class);

        adapter = new SlotAdapter(item -> showSlotDialog(item));
        binding.recyclerSlots.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSlots.setAdapter(adapter);

        viewModel.getClasses().observe(this, list -> classes = list);

        viewModel.getSlots().observe(this, list -> {
            adapter.submit(list);
            binding.tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.fabAddSlot.setOnClickListener(v -> showSlotDialog(null));
    }

    // existing == null means "add a new slot", otherwise "edit this slot"
    private void showSlotDialog(SlotWithClass existing) {
        if (classes.isEmpty()) {
            Toast.makeText(this, "Create a class first", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogSlotBinding d = DialogSlotBinding.inflate(getLayoutInflater());

        // Class spinner
        List<String> classNames = new ArrayList<>();
        for (ClassEntity c : classes) {
            boolean hasCode = c.courseCode != null && !c.courseCode.isEmpty();
            classNames.add(hasCode ? c.name + " (" + c.courseCode + ")" : c.name);
        }
        d.spinnerClass.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, classNames));

        // Day spinner
        List<String> dayNames = new ArrayList<>();
        for (int day : DAY_VALUES) {
            dayNames.add(SlotAdapter.dayName(day));
        }
        d.spinnerDay.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, dayNames));

        // Start time (minutes since midnight). A one-element array so the
        // lambda below can change it.
        final int[] minutes = {9 * 60};

        // Prefill when editing
        if (existing != null) {
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).id.equals(existing.slot.classId)) {
                    d.spinnerClass.setSelection(i);
                }
            }
            for (int i = 0; i < DAY_VALUES.length; i++) {
                if (DAY_VALUES[i] == existing.slot.dayOfWeek) {
                    d.spinnerDay.setSelection(i);
                }
            }
            minutes[0] = existing.slot.startMinutes;
            d.etRoom.setText(existing.slot.room);
        }

        d.btnTime.setText(SlotAdapter.formatTime(minutes[0]));
        d.btnTime.setOnClickListener(v -> new TimePickerDialog(this,
                (view, hour, minute) -> {
                    minutes[0] = hour * 60 + minute;
                    d.btnTime.setText(SlotAdapter.formatTime(minutes[0]));
                },
                minutes[0] / 60, minutes[0] % 60, false).show());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add class slot" : "Edit class slot")
                .setView(d.getRoot())
                .setPositiveButton("Save", null)   // real handler is set after show()
                .setNegativeButton("Cancel", null);
        if (existing != null) {
            builder.setNeutralButton("Delete",
                    (dialog, which) -> confirmDelete(existing.slot.id));
        }
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String room = d.etRoom.getText().toString().trim();
            if (room.isEmpty()) {
                d.etRoom.setError("Room is required");
                return;
            }

            ScheduleSlot slot = new ScheduleSlot();
            slot.id = existing != null ? existing.slot.id : UUID.randomUUID().toString();
            slot.classId = classes.get(d.spinnerClass.getSelectedItemPosition()).id;
            slot.dayOfWeek = DAY_VALUES[d.spinnerDay.getSelectedItemPosition()];
            slot.startMinutes = minutes[0];
            slot.room = room;
            slot.synced = false;

            viewModel.save(slot);
            dialog.dismiss();
        });
    }

    private void confirmDelete(String slotId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete this class slot?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(slotId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}