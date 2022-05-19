package com.onlinemachenic.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlinemachenic.activities.user.DetailedMapActivity;
import com.onlinemachenic.models.Feedback;
import com.onlinemachenic.models.Mechanic;
import com.onlinemachenic.models.MechanicRequest;
import com.onlinemachenic.models.User;

import java.util.ArrayList;
import java.util.BitSet;

import static com.onlinemachenic.activities.mechanic.MechanicDashboardActivity.all_requests_tv;

public class StaticConfig
{
    public static String myMessagingAuth = "AAAAZlFZB1w:APA91bHVbuwJZ2kI7sADoul2SFeEe3PrOm4o5eDEL74Bp3xmmvG1M9r0e1SODbT2NCRIDFXF6iSRnI30JZm-br4cYr--CmB6kAuXVXGg1y6gFeMWKdmyg-6TP-THlWmOJ2XHrbEbqjeX";
    public static DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference().getRef();
    public static DatabaseReference mechanicsRef = mainRef.child("mechanics").getRef();
    public static DatabaseReference usersRef = mainRef.child("users").getRef();
    public static String myUID;
    public static ArrayList<Mechanic> allMechanicsList;
    public static ArrayList<User> allUsersList;
    public static ArrayList<MechanicRequest> allMechanicRequests, allMechanicRequestsSentByMe;
    public static ArrayList<Feedback> myFeedbacksList;
    public static User mUser;
    public static Mechanic mMechanic;
    public static ArrayList<String> mechanicsKeysList, mechanicRequestsKeys, usersKeysList, feedbackKeysList;
    public static Context context;

    public static void fetchMechanicsData()
    {
        mechanicsRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String key = snapshot.getKey();
                if (!mechanicsKeysList.contains(key))
                {
                    mechanicsKeysList.add(key);
                    Mechanic mechanic = snapshot.child("info").getValue(Mechanic.class);
                    allMechanicsList.add(mechanic);

                    if (snapshot.child("requests").exists())
                    {
                        for (DataSnapshot requestSnapshot : snapshot.child("requests").getChildren())
                        {
                            MechanicRequest request = requestSnapshot.getValue(MechanicRequest.class);
                            if (request.getRequest_sender().equals(myUID))
                            {
                                allMechanicRequestsSentByMe.add(request);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String key = snapshot.getKey();
                int index = mechanicsKeysList.indexOf(key);
                Mechanic mechanic = snapshot.child("info").getValue(Mechanic.class);
                allMechanicsList.set(index, mechanic);

                if (snapshot.child("requests").exists())
                {
                    for (DataSnapshot requestSnapshot : snapshot.child("requests").getChildren())
                    {
                        MechanicRequest request = requestSnapshot.getValue(MechanicRequest.class);
                        if (request.getRequest_sender().equals(myUID) && request.getStatus().equals("processing"))
                        {
                            if (context != null)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Important Notice");
                                builder.setMessage("Your request has been approved and " + getMechanicByID(request.getMechanic_id()).getName() + " is on the way. Click OK to see his live location OR you can also Reject");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        dialog.cancel();
                                        Intent intent = new Intent(context, DetailedMapActivity.class);
                                        intent.putExtra("mechanic_id", request.getMechanic_id());
                                        context.startActivity(intent);
                                    }
                                }).setNegativeButton("Reject", new DialogInterface.OnClickListener()
                                {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage("Rejecting");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();

                                        mechanicsRef.child(request.getMechanic_id()).child("requests").child(request.getRequest_id()).child("status").setValue("rejected by user").addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                progressDialog.cancel();
                                                dialog.cancel();
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).show();

                                AlertDialog alertDialog = builder.create();
                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (alertDialog.isShowing())
                                        {
                                            alertDialog.cancel();
                                            Intent intent = new Intent(context, DetailedMapActivity.class);
                                            intent.putExtra("mechanic", request.getMechanic_id());
                                            context.startActivity(intent);

                                        }
                                    }
                                }, 5000);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                int index = mechanicsKeysList.indexOf(snapshot.getKey());
                allMechanicsList.remove(index);
                mechanicsKeysList.remove(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public static void fetchUsersData()
    {
        usersRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String key = snapshot.getKey();
                if (!usersKeysList.contains(key))
                {
                    usersKeysList.add(key);
                    User user = snapshot.child("info").getValue(User.class);
                    allUsersList.add(user);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String key = snapshot.getKey();
                int index = usersKeysList.indexOf(key);
                User user = snapshot.child("info").getValue(User.class);
                allUsersList.set(index, user);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                int index = usersKeysList.indexOf(snapshot.getKey());
                allUsersList.remove(index);
                usersKeysList.remove(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }


    public static void fetchMechanicRequests()
    {
        mechanicsRef.child(myUID).child("requests").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String key = snapshot.getKey();
                if (!mechanicRequestsKeys.contains(key))
                {
                    mechanicRequestsKeys.add(key);
                    allMechanicRequests.add(snapshot.getValue(MechanicRequest.class));
                    if (all_requests_tv != null)
                    {
                        int pending = 0;
                        for (MechanicRequest request: allMechanicRequests)
                        {
                            if (request.getStatus().equals("pending"))
                            {
                                pending++;
                            }
                        }
                        all_requests_tv.setText("Pending Requests (" + pending + ")");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                int index = mechanicRequestsKeys.indexOf(snapshot.getKey());
                allMechanicRequests.set(index, snapshot.getValue(MechanicRequest.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                int index = mechanicRequestsKeys.indexOf(snapshot.getKey());
                allMechanicRequests.remove(index);
                mechanicRequestsKeys.remove(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public static void fetchMyFeedbacks()
    {
        mechanicsRef.child(myUID).child("feedbacks").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (!feedbackKeysList.contains(snapshot.getKey()))
                {
                    feedbackKeysList.add(snapshot.getKey());
                    myFeedbacksList.add(snapshot.getValue(Feedback.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public static void trackUserData(String user_id)
    {
        usersRef.child(user_id).child("info").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                mUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public static void trackMechanicData(String mechanic_id)
    {
        mechanicsRef.child(mechanic_id).child("info").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                mMechanic = snapshot.getValue(Mechanic.class);
                if (DetailedMapActivity.mechanicMarker != null && DetailedMapActivity.mechanicMarkerOptions != null)
                {
                    LatLng latLng = new LatLng(mMechanic.getLatitude(), mMechanic.getLongitude());
                    DetailedMapActivity.mechanicMarker.setPosition(latLng);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public static void setUserData(User user)
    {
        usersRef.child(user.getUser_id()).child("info").setValue(user);
    }

    public static void setMechanicData(Mechanic mechanic)
    {
        mechanicsRef.child(mechanic.getMechanic_id()).child("info").setValue(mechanic);
    }

    public static Mechanic getMechanicByID(String id)
    {
        for (Mechanic mechanic : allMechanicsList)
        {
            if (mechanic.getMechanic_id().equals(id))
            {
                return mechanic;
            }
        }
        return null;
    }

    public static MechanicRequest getRequestByID(String request_id)
    {
        for (MechanicRequest request: allMechanicRequests)
        {
            if (request.getRequest_id().equals(request_id))
            {
                return request;
            }
        }

        return null;
    }
    public static MechanicRequest getMyRequestByID(String request_id)
    {
        for (MechanicRequest request: allMechanicRequestsSentByMe)
        {
            if (request.getRequest_id().equals(request_id))
            {
                return request;
            }
        }

        return null;
    }

    public static ArrayList<Mechanic> getApprovedMechanics()
    {
        ArrayList<Mechanic> approvedMechanics = new ArrayList<>();
        for (Mechanic mechanic: allMechanicsList)
        {
            if (mechanic.isApproved())
            {
                approvedMechanics.add(mechanic);
            }
        }

        return approvedMechanics;
    }

}