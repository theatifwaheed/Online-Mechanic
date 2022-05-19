package com.onlinemachenic.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.onlinemachenic.R;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.Mechanic;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.onlinemachenic.helpers.StaticConfig.mUser;

public class MechanicDetailsActivity extends AppCompatActivity
{
    private Mechanic mechanic;
    private TextView mechanic_name, mechanic_shop, mechanic_contact, shop_distance, mechanic_email;
    private CircleImageView mechanic_image;
    double roundOff;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_details);

        String selected_mechanic_id = getIntent().getStringExtra("selected_mechanic_id");
        mechanic = StaticConfig.getMechanicByID(selected_mechanic_id);


        mechanic_name = findViewById(R.id.mechanic_name);
        mechanic_shop = findViewById(R.id.mechanic_shop);
        mechanic_contact = findViewById(R.id.mechanic_contact);
        shop_distance = findViewById(R.id.shop_distance);
        mechanic_image = findViewById(R.id.mechanic_image);
        mechanic_email = findViewById(R.id.mechanic_email);

        Picasso.get().load(mechanic.getPic_url()).into(mechanic_image);
        mechanic_email.setText(mechanic.getEmail());


        roundOff = 0;
        double my_latitude = mUser.getLatitude();
        double my_longitude = mUser.getLongitude();
        double distance = meterDistanceBetweenPoints((float) my_latitude, (float) my_longitude, (float) mechanic.getLatitude(), (float) mechanic.getLongitude());
        double kilo_distance = distance / 1000;
        roundOff = Math.round(kilo_distance * 100.0) / 100.0;

        mechanic_name.setText(mechanic.getName());
        mechanic_shop.setText(mechanic.getShop_name());
        mechanic_contact.setText(mechanic.getPhone_number());
        shop_distance.setText((roundOff) + " km");

    }

    public void hireMechanic(View view)
    {
        startActivity(new Intent(this, HireMechanicActivity.class).putExtra("selected_mechanic_id", mechanic.getMechanic_id()).putExtra("distance", roundOff));
    }


    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b)
    {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public void shareQRCode(View view)
    {
        startActivity(new Intent(this, QRCodeActivity.class).putExtra("mechanic_id", mechanic.getMechanic_id()).putExtra("mechanic_name", mechanic.getName()));
    }
}