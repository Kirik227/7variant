package com.example.taskmanager4result;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapter.TaskAdapter;
import db.DatabaseHelper;
import model.Task;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private Spinner spinnerFilter;
    private FloatingActionButton fabAddTask;
    private DatabaseHelper databaseHelper;
    private model.Task Task;
    private List<Task> tasks;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, TaskService.class);
        startService(serviceIntent);
        databaseHelper = new DatabaseHelper(this);
        List<Task> tasks = databaseHelper.getAllTasks();
        for (Task task : tasks) {
            Log.d("Task", "Title: " + task.getTitle());
        }
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        fabAddTask = findViewById(R.id.fabAddTask);
        recyclerViewTasks.setAdapter(taskAdapter);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView taskNameTextView = findViewById(R.id.textViewTitle);
        if (taskNameTextView != null) {
            taskNameTextView.setText("Your Task Name");
        } else {
            Log.e("TAG", "TextView with id task_name is not found");
        }
        getAllTasks();
        setupRecyclerView();
        setupSpinner();
        setupFab();
        editTask(Task);
        loadTasks();
    }

    private void editTask(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("Task", task);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>(), databaseHelper);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupFab() {
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });
    }

    private void loadTasks() {
        List<Task> tasks = databaseHelper.getAllTasks(); // Получение всех задач

        if (tasks != null && !tasks.isEmpty()) {
            taskAdapter.updateTasks(tasks); // Обновите адаптер с новыми задачами
            Log.i("MainActivity", "Tasks loaded: " + tasks.size());
        } else {
            Log.e("MainActivity", "No tasks found or tasks list is null");
        }

    }
private void getAllTasks(){
    List<Task> tasks = databaseHelper.getAllTasks(); // Получение всех задач
    if (tasks == null) {
        Log.e("MainActivity", "Tasks list is null");
    } else if (tasks.isEmpty()) {
        Log.e("MainActivity", "No tasks found");
    } else {
        Log.i("MainActivity", "Tasks loaded: " + tasks.size());
    }
}
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    public void onTaskClick(model.Task task) {

    }
}

