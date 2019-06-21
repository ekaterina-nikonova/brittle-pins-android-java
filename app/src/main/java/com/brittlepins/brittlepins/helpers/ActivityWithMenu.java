package com.brittlepins.brittlepins.helpers;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.brittlepins.brittlepins.R;

import java.util.HashMap;

public class ActivityWithMenu extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logOutMenuItem:
                AuthServices.logOut(this);
                return true;
            case R.id.newBoardMenuItem:
                BoardActions.create(this, new HashMap<String, String>());
                return true;
            default:
                return false;
        }
    }
}
