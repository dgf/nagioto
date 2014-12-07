package de.g2d.nagioto.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.g2d.nagioto.R;
import de.g2d.nagioto.domain.Host;

public class ServerList extends Fragment {

    private List<Host> servers;
    private Adapter adapter;
    private LayoutInflater layoutInflater;

    public void update(List<Host> servers) {
        this.servers = servers;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        adapter = new Adapter();
        servers = new ArrayList<>();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        View view = inflater.inflate(R.layout.serverlist, null);
        ListView listView = (ListView) view.findViewById(R.id.serverListView);
        listView.setAdapter(adapter);
        return view;
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return servers.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Host server = servers.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.serverentry, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.hostname);
            tv.setText(server.name);
            return convertView;
        }
    }
}
