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
import com.google.zxing.RGBLuminanceSource;
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

    public static Result scanQRCode(Bitmap bitmap) {
        try {
            QRCodeReader reader = new QRCodeReader();
            int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
            // copy pixel data from the Bitmap into the 'intArray' array
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            return reader.decode(binaryBitmap, new HashMap<DecodeHintType, Object>());
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
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
