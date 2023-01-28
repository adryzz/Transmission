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

import com.google.android.material.snackbar.Snackbar;

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
            Snackbar.make(scan, "QR code scanning or generation not implemented yet.", Snackbar.LENGTH_LONG).show();
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
            ImageView view = findViewById(R.id.qr_view);
            view.setImageBitmap(imageBitmap);
            Snackbar.make(view, "QR code scanning or generation not implemented yet.", Snackbar.LENGTH_LONG).show();
        }
    }
}