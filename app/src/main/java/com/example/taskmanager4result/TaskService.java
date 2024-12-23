package com.example.taskmanager4result;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import db.DatabaseHelper;
import model.Task;

public class TaskService extends Service {

    private static final String CHANNEL_ID = "TaskManagerChannel";
    private static final int NOTIFICATION_ID = 1;

    private ScheduledExecutorService scheduler;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::checkDueTasks, 0, 1, TimeUnit.HOURS);
        return START_STICKY;
    }

    private void checkDueTasks() {
        List<Task> tasks = databaseHelper.getAllTasks();
        long currentTime = System.currentTimeMillis();

        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDueDate().getTime() - currentTime <= 24 * 60 * 60 * 1000) {
                showNotification(task);
            }
        }
    }

    @SuppressLint("NotificationPermission")
    private void showNotification(Task task) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Task Due Soon")
                .setContentText(task.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = "Task Manager Channel";
        String description = "Channel for Task Manager notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}