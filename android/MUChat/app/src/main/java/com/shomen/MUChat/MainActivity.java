package com.shomen.MUChat;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.Fragments.LoginFragment;
import com.shomen.MUChat.Fragments.SongListFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();
    private LoginSession loginSession ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate method");
        if(loginSession == null){
            Log.d(LOG_TAG,"its null....");
        }
        loginSession = new LoginSession(this);
        invalidateOptionsMenu();

        setContentView(R.layout.activity_main);

        if(loginSession.isLoggedIn()){
            SongListFragment fragment = new SongListFragment();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.main_act_fragment_container,fragment,"SongListFrag").
                    commit();
        }else{
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.main_act_fragment_container,fragment,"LoginFragment").
                    commit();
        }

    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "inside onStart method");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "inside onResume method");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "inside onPause method");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "inside onStop method");
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "inside onDestroy method");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "inside onCreateOptionsMenu method");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        if(loginSession.isLoggedIn()){
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        }else{
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "inside onPrepareOptionsMenu method");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_login) {
            LoginFragment fragment = new LoginFragment();

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.main_act_fragment_container, fragment,"LoginFragment");
            ft.addToBackStack("LoginFragment");
            ft.commit();
            return true;
        }

        if(id == R.id.action_logout){

            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Refresh option menu
                            invalidateOptionsMenu();
                            loginSession.logoutUser();
                            clearBackStack();
                            LoginFragment fragment = new LoginFragment();
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.main_act_fragment_container,fragment,"LoginFragment").
                                    commit();
                            Toast.makeText(MainActivity.this, "You have logged out",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
