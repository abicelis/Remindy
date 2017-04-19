package ve.com.abicelis.remindy.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by abice on 17/4/2017.
 */

public class ImageUtil {

    /**
     * Returns a bitmap of the image contained in the byte array
     * @param imgInBytes The image in a byte[]
     */
    public static Bitmap getBitmap(byte[] imgInBytes) {
        return BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
    }

    /**
     * Returns a bitmap of the image at the specified path
     * @param path The path to the image
     */
    public static Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * Returns a bitmap of the image at the specified path
     * @param file The file containing the path to the image
     */
    public static Bitmap getBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * Returns a bitmap of the image at the specified uri
     * @param uri The URI containing the path to the image
     */
    public static Bitmap getBitmap(Uri uri, Activity activity) throws IOException {
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
    }


    /**
     * Bitmap to byte[]
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Returns a compressed JPEG byte[] representation of a Bitmap
     * @param bitmap  Bitmap
     * @param quality int
     * @return compressed JPEG byte array
     */
    public static byte[] toCompressedByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }


    /**
     * Saves a JPEG at the given quality to disk at the specified path of the given File
     * @param file The file where the JPEG will be saved
     * @param bitmapToSave The Bitmap to save into the file
     * @param quality The percentage of JPEG compression
     */
    public static void saveBitmapAsJpeg(File file, Bitmap bitmapToSave, int quality) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(toCompressedByteArray(bitmapToSave, quality));
        fos.close();
    }




    /**
     * Returns a scaled Bitmap with:
     *  - Its larger dimension = largerScaledDimension in px
     *  - Its smaller dimension scaled, according to the bitmap's original aspect ratio
     *
     *  Note: if the bitmap's dimensions are already smaller than largerScaledDimension
     *  then nothing will be done to the bitmap
     */
    public static Bitmap scaleBitmap(Bitmap image, int largerScaledDimension) {

        if (image == null || image.getWidth() == 0 || image.getHeight() == 0)
            return image;

        //if the image is already small, leave as is
        if (image.getHeight() <= largerScaledDimension && image.getWidth() <= largerScaledDimension)
            return image;

        // Resize the larger dimension of the image to largerScaledDimension and calculate other size
        // respecting the image's aspect ratio
        boolean heightLargerThanWidth = (image.getHeight() > image.getWidth());
        float aspectRatio = (heightLargerThanWidth ? (float)image.getHeight() / (float)image.getWidth() : (float)image.getWidth() / (float)image.getHeight());
        int smallerScaledDimension = (int) (largerScaledDimension / aspectRatio);
        int scaledWidth = (heightLargerThanWidth ? smallerScaledDimension : largerScaledDimension);
        int scaledHeight = (heightLargerThanWidth ? largerScaledDimension : smallerScaledDimension);

        return Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, true);
    }

}
