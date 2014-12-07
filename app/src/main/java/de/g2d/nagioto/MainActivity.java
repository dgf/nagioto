package de.g2d.nagioto;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.g2d.nagioto.core.BackgroundService;

import de.g2d.nagioto.domain.Settings;
import de.g2d.nagioto.domain.Status;
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
        backgroundService.startPolling(settings);
    }

    @Override
    protected void onResume() {
        if (serviceBound) {
            backgroundService.fetchStatus(settings, this);
        }
        super.onResume();
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
                    if (serviceBound) {
                        backgroundService.fetchStatus(settings, MainActivity.this);
                    }
                }
            }
        });

        serverList = new ServerList();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, serverList)
                    .commit();
        }


        settings = loadSettings();

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

    private void showSettings(final Settings settings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View settingsView = getLayoutInflater().inflate(R.layout.settings, null);
        alertDialog.setView(settingsView);
        final EditText etUrl = (EditText) settingsView.findViewById(R.id.url);
        final EditText etUsername = (EditText) settingsView.findViewById(R.id.username);
        final EditText etPassword = (EditText) settingsView.findViewById(R.id.password);
        etUrl.setText(settings.getUrl());
        etUsername.setText(settings.getUsername());
        etPassword.setText(settings.getPassword());

        Button button = (Button) settingsView.findViewById(R.id.connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setPassword(etPassword.getText().toString());
                settings.setUsername(etUsername.getText().toString());
                settings.setUrl(etUrl.getText().toString());
                saveSettings(settings);
                if (serviceBound) {
                    backgroundService.fetchStatus(settings, MainActivity.this);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void saveSettings(Settings settings) {
        SharedPreferences sp = getSharedPreferences("nagioto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("url", settings.getUrl());
        editor.putString("username", settings.getUsername());
        editor.putString("password", settings.getPassword());
        editor.commit();
    }

    public Settings loadSettings() {
        Settings settings = new Settings();
        SharedPreferences sp = getSharedPreferences("nagioto", Context.MODE_PRIVATE);
        settings.setUrl(sp.getString("url", "http://127.0.0.1/cgi-bin/icinga/"));
        settings.setUsername(sp.getString("username", "icke"));
        settings.setPassword(sp.getString("password", "secret"));
        return settings;
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
                showSettings(settings);
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
}
