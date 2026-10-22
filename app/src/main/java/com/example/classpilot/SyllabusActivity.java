package com.example.classpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.databinding.ActivitySyllabusBinding;
import com.example.classpilot.ui.LessonAdapter;
import com.example.classpilot.viewmodel.LessonViewModel;

public class SyllabusActivity extends AppCompatActivity {
    private static final String TAG = "SyllabusActivity";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_CLASS_NAME = "class_name";

    private ActivitySyllabusBinding binding;
    private LessonViewModel viewModel;
    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivitySyllabusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        String className = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        if (classId == null) {
            Toast.makeText(this, "No class selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Syllabus covered");
            getSupportActionBar().setSubtitle(className);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Same ViewModel, Adapter and row layout as the Today's lesson screen.
        // The date is not needed here, so 0 is passed.
        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        viewModel.init(classId, 0L);

        adapter = new LessonAdapter();
        binding.recyclerSyllabus.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSyllabus.setAdapter(adapter);

        // All lessons of this class, newest first (the DAO query sorts them)
        viewModel.getLessons().observe(this, list -> {
            adapter.submit(list);
            binding.tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
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