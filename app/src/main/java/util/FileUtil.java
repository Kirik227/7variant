package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    private static final String IMAGE_DIRECTORY = "task_images";

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) throws IOException {
        // Create a file to save the image
        File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";
        File file = new File(directory, fileName);

        // Save the image
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        return file.getAbsolutePath();
    }

    public static boolean deleteImage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            return file.exists() && file.delete();
        }
        return false;
    }

    public static Uri getUriForFile(Context context, String imagePath) {
        File file = new File(imagePath);
        return Uri.fromFile(file);
    }
}

