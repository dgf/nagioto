package de.g2d.nagioto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.g2d.nagioto.domain.Cgi2Response;
import de.g2d.nagioto.domain.Settings;
import de.g2d.nagioto.view.ServerList;


public class MainActivity extends ActionBarActivity {
    public final static String QUERY = "status.cgi?jsonoutput&style=hostdetail";
    private ServerList serverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "create main activity");
        setContentView(R.layout.activity_main);

        serverList = new ServerList();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, serverList)
                    .commit();
        }
        Settings settings = loadSettings();
        showSettings(settings);
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
                RequestTask requestTask = new RequestTask(MainActivity.this, new StatusCallback() {
                    @Override
                    public void onFinish(Cgi2Response cgiResponse) {
                        serverList.update(cgiResponse.status.servers);
                        Log.d("MainActivity", "UI callback to fill list - yeah");
                    }
                });
                requestTask.execute(settings);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.serverlist, container, false);
            return rootView;
        }
    }

}
