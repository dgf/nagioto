package de.g2d.nagioto;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import de.g2d.nagioto.core.BackgroundService;
import de.g2d.nagioto.domain.Settings;
import de.g2d.nagioto.domain.Status;
import de.g2d.nagioto.view.Info;
import de.g2d.nagioto.view.ServerList;


public class MainActivity extends ActionBarActivity implements UiCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ServerList serverList;
    private Settings settings;
    private BackgroundService backgroundService;

    private boolean serviceBound;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) service;
            backgroundService = binder.getService();
            serviceBound = true;
            executeAfterServiceConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            Log.d(TAG, ">>> BackgroundService unbind from activity");
        }
    };

    private void executeAfterServiceConnect() {
        Log.d(TAG, ">>> BackgroundService bind to activity");
        backgroundService.fetchStatus(settings, this);
        backgroundService.startPolling(settings, this);
    }

    @Override
    protected void onResume() {
        if (serviceBound) {
            backgroundService.fetchStatus(settings, this);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int countOfFragments = fragmentManager.getBackStackEntryCount();
        if (Utils.isActiveFragment(fragmentManager, Info.TAG)) {
            for (int i = countOfFragments - 1; i >= 0; i--) {
                fragmentManager.popBackStackImmediate();
            }
        }
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "create main activity");
        setContentView(R.layout.activity_main);

        findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound) {
                    backgroundService.fetchStatus(settings, MainActivity.this);
                }
            }
        });

        serverList = new ServerList();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, serverList)
                    .commit();
        }


        loadSettingPreferences();

        // start background service
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceBound) {
            backgroundService.stopSelf();
            unbindService(serviceConnection);
        }
    }

    private void showError(Throwable throwable) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Info fragment = (Info) fragmentManager.findFragmentByTag(Info.TAG);
        if (fragment == null) {
            fragment = new Info();
            fragment.setThrowable(throwable);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment, Info.TAG);
        transaction.addToBackStack(Info.TAG);
        transaction.commit();
    }

    private void showSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View settingsView = getLayoutInflater().inflate(R.layout.settings, null);
        alertDialog.setView(settingsView);
        final EditText etUrl = (EditText) settingsView.findViewById(R.id.url);
        final EditText etUsername = (EditText) settingsView.findViewById(R.id.username);
        final EditText etPassword = (EditText) settingsView.findViewById(R.id.password);
        final EditText etSeconds = (EditText) settingsView.findViewById(R.id.seconds);
        final ToggleButton tbDemo = (ToggleButton) settingsView.findViewById(R.id.demo);
        etUrl.setText(settings.getUrl());
        etUsername.setText(settings.getUsername());
        etPassword.setText(settings.getPassword());
        etSeconds.setText(settings.getSeconds().toString());
        tbDemo.setChecked(settings.isDemo());

        Button button = (Button) settingsView.findViewById(R.id.connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setPassword(etPassword.getText().toString());
                settings.setUsername(etUsername.getText().toString());
                settings.setSeconds(Integer.parseInt(etSeconds.getText().toString()));
                settings.setUrl(etUrl.getText().toString());
                settings.setDemo(tbDemo.isChecked());
                saveSettingPreferences();
                if (serviceBound) {
                    backgroundService.fetchStatus(settings, MainActivity.this);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void saveSettingPreferences() {
        SharedPreferences sp = getSharedPreferences("nagioto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("url", settings.getUrl());
        editor.putString("username", settings.getUsername());
        editor.putString("password", settings.getPassword());
        editor.putInt("seconds", settings.getSeconds());
        editor.putBoolean("demo", settings.isDemo());
        editor.commit();
    }

    private void loadSettingPreferences() {
        settings = new Settings();
        SharedPreferences sp = getSharedPreferences("nagioto", Context.MODE_PRIVATE);
        settings.setUrl(sp.getString("url", "http://127.0.0.1/cgi-bin/icinga/"));
        settings.setUsername(sp.getString("username", "icke"));
        settings.setPassword(sp.getString("password", "secret"));
        settings.setSeconds(sp.getInt("seconds", 37));
        settings.setDemo(sp.getBoolean("demo", true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, Integer.toString(menu.size()));
        MenuItem item = menu.getItem(0);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSettings();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusResponse(Status status) {
        serverList.update(status.hosts);
    }

    @Override
    public void onError(Throwable throwable) {
        showError(throwable);
    }

    public void onToggleDemoClicked(View view) {
        ToggleButton tbDemo = (ToggleButton) view.findViewById(R.id.demo);
        Log.d(TAG, "TOGGLE Demo Settings: " + tbDemo.isChecked());
        settings.setDemo(tbDemo.isChecked());
        saveSettingPreferences();
    }
}
