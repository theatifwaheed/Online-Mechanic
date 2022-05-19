package com.onlinemachenic.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.onlinemachenic.R;
import com.onlinemachenic.adapters.NearbyMechanicsAdapter;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.Mechanic;

import java.util.ArrayList;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE;


public class AllMechanicsActivity extends AppCompatActivity
{

    private RecyclerView mechanics_recycler_view;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_mechanics);
        toolbar = findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.mechanics_search_menu);

        mechanics_recycler_view = findViewById(R.id.mechanics_recycler_view);
        NearbyMechanicsAdapter adapter = new NearbyMechanicsAdapter(this, StaticConfig.getApprovedMechanics());
        mechanics_recycler_view.setAdapter(adapter);
        mechanics_recycler_view.setLayoutManager(new LinearLayoutManager(this));


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                searchItem = item;

                if (item.getItemId() == R.id.action_search)
                {

                    searchView = (SearchView) item.getActionView();
                    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    searchView.setQueryHint("Mechanic Name");

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
                    {
                        @Override
                        public boolean onQueryTextSubmit(String query)
                        {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText)
                        {


                            adapter.getFilter().filter(newText);


                            return false;
                        }
                    });
                }

                return false;
            }
        });

    }

    @Override
    public void onBackPressed()
    {

        if (searchItem != null && searchView != null)
        {
            if (!searchView.isIconified())
            {
                MenuItemCompat.collapseActionView(searchItem);
            }
            else
            {
                super.onBackPressed();
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void searchQRCode(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(AllMechanicsActivity.this);
        integrator.setPrompt("Scan Mechanic's QR Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setDesiredBarcodeFormats(QR_CODE);
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
            else
            {

                String rec_ID = result.getContents();
                Mechanic mechanic = StaticConfig.getMechanicByID(rec_ID);

                if (mechanic != null)
                {
                    Intent intent = new Intent(AllMechanicsActivity.this, MechanicDetailsActivity.class);
                    intent.putExtra("selected_mechanic_id", mechanic.getMechanic_id());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "Please scan a valid QR Code", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


}