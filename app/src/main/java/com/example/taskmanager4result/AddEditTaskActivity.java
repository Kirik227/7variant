package com.example.taskmanager4result;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import db.DatabaseHelper;
import model.Task;
import util.FileUtil;

public class AddEditTaskActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private TextView textViewSelectedDate;
    private ImageView imageViewTaskPhoto;

    private DatabaseHelper databaseHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private String imagePath;
    private Task existingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);


        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonSetDueDate = findViewById(R.id.buttonSetDueDate);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        imageViewTaskPhoto = findViewById(R.id.imageViewTaskPhoto);
        Button buttonSaveTask = findViewById(R.id.buttonSaveTask);


        databaseHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        buttonSetDueDate.setOnClickListener(v -> showDatePickerDialog());
        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        buttonSaveTask.setOnClickListener(v -> saveTask());


        existingTask = (Task) getIntent().getSerializableExtra("ЗАДАЧА");
        if (existingTask != null) {
            setTitle("Редактировать задачу");
            populateFields();
        } else {
            setTitle("Добавить новую задачу");
        }


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateDisplay();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateDisplay() {
        textViewSelectedDate.setText(dateFormatter.format(calendar.getTime()));
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            return;
        }
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            imageViewTaskPhoto.setImageBitmap(imageBitmap);

            // Save the image to internal storage
            try {
                imagePath = FileUtil.saveImageToInternalStorage(this, imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Не удалось сохранить изображение.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTask() {
        String title = Objects.requireNonNull(editTextTitle.getText()).toString().trim();
        String description = Objects.requireNonNull(editTextDescription.getText()).toString().trim();
        Date dueDate = calendar.getTime();

        if (title.isEmpty()) {
            editTextTitle.setError("Название обязательно");
            return;
        }

        Task task = existingTask != null ? existingTask : new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setImagePath(imagePath);

        long result;
        if (existingTask != null) {
            result = databaseHelper.updateTask(task);
        } else {
            result = databaseHelper.insertTask(task);
        }

        if (result != -1) {
            Toast.makeText(this, "Задача успешно сохранена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Не удалось сохранить задачу", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFields() {
        editTextTitle.setText(existingTask.getTitle());
        editTextDescription.setText(existingTask.getDescription());
        calendar.setTime(existingTask.getDueDate());
        updateDateDisplay();
        if (existingTask.getImagePath() != null) {
            imagePath = existingTask.getImagePath();
            Uri imageUri = FileUtil.getUriForFile(this, imagePath);
            imageViewTaskPhoto.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Освобождение ресурсов
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Освобождение ресурсов
    }
}

