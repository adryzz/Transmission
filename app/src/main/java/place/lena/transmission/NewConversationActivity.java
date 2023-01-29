package place.lena.transmission;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class NewConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        Button scan = findViewById(R.id.scan_qr_button);
        scan.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 1);
            } else {
                Snackbar.make(scan, "You got no camera app.", Snackbar.LENGTH_LONG).show();
            }
        });

        Button gen = findViewById(R.id.generate_qr_button);
        gen.setOnClickListener(v -> {

            //TODO: make it actually generate useful stuff
            Random rng = new Random();
            byte[] arr = new byte[128];
            rng.nextBytes(arr);
            ImageView view = findViewById(R.id.qr_view);
            Bitmap bmp = Utils.generateQRCode(new String(arr, StandardCharsets.UTF_8), 512, 512);
            if (bmp == null) {
                Snackbar.make(scan, "Error while generating QR code.", Snackbar.LENGTH_LONG).show();
                return;
            }
            view.setImageBitmap(bmp);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Do something with the image, such as displaying it in an ImageView.
            String res = Utils.scanQRCode(imageBitmap);

            if (res == null) {
                Snackbar.make(findViewById(R.id.generate_qr_button), "Couldn't read QR code.", Snackbar.LENGTH_LONG).show();
                return;
            }

            //TODO: do stuff with the QR code
        }
    }
}