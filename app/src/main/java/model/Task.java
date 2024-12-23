package model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private long id;
    private String name;
    private String title;
    private String description;
    private Date dueDate;
    private boolean isCompleted;
    private String imagePath;

    public Task() {
    }

    public Task(long id, String title, String description, Date dueDate, boolean isCompleted, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.imagePath = imagePath;
    }
    public Task(String name) {
        this.name = name;
    }


    public String getName() {
        return title;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @NonNull
    @Override
    public String toString() {
        return "Задача{" +
                "id=" + id +
                ", заголовок='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", Завершено=" + isCompleted +
                ", Путь к изображению='" + imagePath + '\'' +
                '}';
    }
}

