package com.onlinemachenic.activities.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onlinemachenic.R;
import com.onlinemachenic.helpers.FCMMessages;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.Mechanic;
import com.onlinemachenic.models.MechanicRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.onlinemachenic.helpers.StaticConfig.mUser;
import static com.onlinemachenic.helpers.StaticConfig.mechanicsRef;
import static com.onlinemachenic.helpers.StaticConfig.myUID;

public class HireMechanicActivity extends AppCompatActivity
{

    private EditText edt_name, edt_vehicle, edt_issue;
    private Mechanic mechanic;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_mechanic);

        edt_name = findViewById(R.id.edt_name);
        edt_vehicle = findViewById(R.id.edt_vehicle);
        edt_issue = findViewById(R.id.edt_issue);

        mechanic = StaticConfig.getMechanicByID(getIntent().getStringExtra("selected_mechanic_id"));
        distance = getIntent().getDoubleExtra("distance", 0);
        edt_name.setText(mUser.getName());

    }

    public void submitHireRequest(View view)
    {
        String name = edt_name.getText().toString(), vehicle = edt_vehicle.getText().toString(), issue = edt_issue.getText().toString();
        if (name.isEmpty() || vehicle.isEmpty() || issue.isEmpty())
        {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Hire Request");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String date = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
        String req_id = System.currentTimeMillis() + "";



        String text_body = mUser.getName() + " is " + distance + " km away from your location. Do you want to go for him ?";
        MechanicRequest request = new MechanicRequest(req_id, mechanic.getMechanic_id(), myUID, date, text_body, "pending", issue, vehicle);

        mechanicsRef.child(mechanic.getMechanic_id()).child("requests").child(req_id).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                progressDialog.cancel();
                if (task.isSuccessful())
                {
                    new FCMMessages().sendMessageSingle(HireMechanicActivity.this, mechanic.getNotification_token(), mUser.getName() + " wants to hire you", text_body, null);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(HireMechanicActivity.this);
                    builder1.setTitle("Request Sent");
                    builder1.setMessage("Please wait at least 5 minutes until the mechanic responds");
                    builder1.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    });
                    builder1.show();
                    builder1.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(HireMechanicActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}