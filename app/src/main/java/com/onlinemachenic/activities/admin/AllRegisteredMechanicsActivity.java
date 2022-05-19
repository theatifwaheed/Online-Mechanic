package com.onlinemachenic.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.onlinemachenic.R;
import com.onlinemachenic.adapters.AllMechanicsAdapter;
import com.onlinemachenic.adapters.NearbyMechanicsAdapter;
import com.onlinemachenic.helpers.StaticConfig;

import java.util.ArrayList;

public class AllRegisteredMechanicsActivity extends AppCompatActivity
{

    private RecyclerView mechanics_recycler_view;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_registered_mechanics);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.mechanics_search_menu);

        mechanics_recycler_view = findViewById(R.id.mechanics_recycler_view);
        AllMechanicsAdapter adapter = new AllMechanicsAdapter(this, new ArrayList<>(StaticConfig.allMechanicsList));
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

}