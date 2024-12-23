package adapter;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager4result.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import db.DatabaseHelper;
import model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private OnTaskClickListener onTaskClickListener;
    private List<Task> tasks;
    private final SimpleDateFormat dateFormat;
    private final DatabaseHelper databaseHelper;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
    public void updateTasks(List<Task> newTasks) {
        this.tasks.clear(); // Очистите старые задачи
        this.tasks.addAll(newTasks); // Добавьте новые задачи
        notifyDataSetChanged(); // Уведомите адаптер об изменениях
    }
    public TaskAdapter(List<Task> tasks, DatabaseHelper databaseHelper) {
        this.tasks = tasks;
        this.databaseHelper = databaseHelper;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.onTaskClickListener = listener; // Убедитесь, что listener передан
    }

    @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.textViewTitle.setText(task.getName());
        holder.textViewDueDate.setText(String.format("Срок: %s", dateFormat.format(task.getDueDate())));
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Обработчики кликов
        holder.checkBoxCompleted.setOnClickListener(v -> {
            task.setCompleted(holder.checkBoxCompleted.isChecked());
            databaseHelper.updateTask(task);
        });

        holder.itemView.setOnClickListener(v -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }
    private void loadTasks() {
        List<Task> tasks = databaseHelper.getAllTasks(); // Метод получения всех задач

        if (tasks != null && !tasks.isEmpty()) {
           updateTasks(tasks);
            Log.i("MainActivity", "Tasks loaded: " + tasks.size());
        } else {
            Log.e("MainActivity", "No tasks found or tasks list is null");
        }
    }
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewDueDate;
        CheckBox checkBoxCompleted;


        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle); // Убедитесь, что это правильный ID
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
        public void bind(Task task, OnTaskClickListener listener) {
            textViewTitle.setText(task.getName());

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
}

