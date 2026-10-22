package com.example.classpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.databinding.ActivityMainBinding;
import com.example.classpilot.databinding.DialogAddClassBinding;
import com.example.classpilot.ui.ClassAdapter;
import com.example.classpilot.viewmodel.ClassViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MAX_STUDENTS = 500;

    private ActivityMainBinding binding;
    private ClassViewModel viewModel;
    private ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        // EXPLICIT INTENT: open the class menu and pass the class id + name
        adapter = new ClassAdapter(c -> {
            Intent intent = new Intent(MainActivity.this, ClassMenuActivity.class);
            intent.putExtra(ClassMenuActivity.EXTRA_CLASS_ID, c.id);
            intent.putExtra(ClassMenuActivity.EXTRA_CLASS_NAME, c.name);
            startActivity(intent);
        });
        binding.recyclerClasses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerClasses.setAdapter(adapter);

        viewModel.getClasses().observe(this, list -> {
            adapter.submit(list);
            binding.tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.fabAdd.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        DialogAddClassBinding d = DialogAddClassBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New class")
                .setView(d.getRoot())
                .setPositiveButton("Save", null)   // real click handler is set below
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        // Set the Save handler AFTER show(), so a wrong input keeps the dialog
        // open (with the error message) instead of closing it and losing the text.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = d.etName.getText().toString().trim();
            String code = d.etCode.getText().toString().trim();
            String countText = d.etStudents.getText().toString().trim();

            if (name.isEmpty()) {
                d.etName.setError("Class name is required");
                return;
            }

            int count = 0;
            try {
                count = Integer.parseInt(countText);
            } catch (NumberFormatException ignored) {
            }
            if (count < 1 || count > MAX_STUDENTS) {
                d.etStudents.setError("Enter a number from 1 to " + MAX_STUDENTS);
                return;
            }

            viewModel.addClass(name, code, count);
            dialog.dismiss();
        });
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}