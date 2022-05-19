package com.onlinemachenic.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlinemachenic.R;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRCodeActivity extends AppCompatActivity
{
    private ImageView mechanic_qr_code;
    private TextView mechanic_name;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        mechanic_name = findViewById(R.id.mechanic_name);
        mechanic_qr_code = findViewById(R.id.mechanic_qr_code);

        String mechanic_id = getIntent().getStringExtra("mechanic_id");
        mechanic_name.setText(getIntent().getStringExtra("mechanic_name"));

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(mechanic_id, null, QRGContents.Type.TEXT, smallerDimension);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        // Getting QR-Code as Bitmap
        Bitmap bitmap = qrgEncoder.getBitmap();
        // Setting Bitmap to ImageView
        mechanic_qr_code.setImageBitmap(bitmap);
    }
}