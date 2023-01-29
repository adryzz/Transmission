package place.lena.transmission;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.preference.PreferenceManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import place.lena.transmission.R;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    static String timestampToText(long timestamp) {

        long now = Instant.now().getEpochSecond();

        long difference = now - timestamp;

        if (difference < 60) {
            return "Now";
        }

        if (difference < 3600) {
            return (difference / 60) + "m";
        }

        if (difference < 86400) {
            return (difference / 3600) + "h";
        }

        if (difference < 604800) {
            return (difference / 86400) + "d";
        }

        return Instant.ofEpochSecond(timestamp).toString();
    }

    static int rssiToIconId(int rssi){
        if (rssi >= -55){
            return R.drawable.round_signal_cellular_4_bar_24;
        }

        if (rssi >= -66){
            return R.drawable.round_signal_cellular_3_bar_24;
        }

        if (rssi >= -77){
            return R.drawable.round_signal_cellular_2_bar_24;
        }

        if (rssi >= -88){
            return R.drawable.round_signal_cellular_1_bar_24;
        }
        return R.drawable.round_signal_cellular_0_bar_24;
    }

    public static int setFlag(int source, int flag) {
        return source | flag;
    }

    public static int unsetFlag(int source, int flag) {
        return source & ~flag;
    }

    public static boolean getFlag(int source, int flag) {
        return (source & flag) == flag;
    }

    public static void clearSettings(Context context, int resId) {
        
    }

    public static boolean getPreferenceBool(Context ctx, int id, int defaultId) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).
                getBoolean(ctx.getString(id), ctx.getResources().getBoolean(defaultId));
    }

    public static int getPreferenceInt(Context ctx, int id, int defaultId) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).
                getInt(ctx.getString(id), ctx.getResources().getInteger(defaultId));
    }

    public static String scanQRCode(Bitmap bitmap) {
        QRCodeReader reader = new QRCodeReader();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1"); // set character set to ISO-8859-1 for binary data
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(toLuminanceSource(bitmap)));
        try {
            Result result = reader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return null;
    }

    static LuminanceSource toLuminanceSource(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        byte[] yuv = new byte[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[i * width + j];
                yuv[i * width + j] = (byte) grey;
            }
        }
        return new PlanarYUVLuminanceSource(yuv, width, height, 0, 0, width, height, false);
    }

    public static Bitmap generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1"); // set character set to ISO-8859-1 for binary data
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
