package de.g2d.nagioto.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.g2d.nagioto.R;
import de.g2d.nagioto.domain.Host;
import de.g2d.nagioto.domain.Service;

public class ServerList extends Fragment {

    private static final String TAG = ServerList.class.getSimpleName();
    private List<Host> servers;
    private HostAdapter hostAdapter;
    private LayoutInflater layoutInflater;
    private DateFormat dateFormat;

    public void update(List<Host> servers) {
        this.servers = servers;
        hostAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        hostAdapter = new HostAdapter();
        servers = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        View view = inflater.inflate(R.layout.serverlist, null);
        ListView listView = (ListView) view.findViewById(R.id.serverListView);
        listView.setAdapter(hostAdapter);
        return view;
    }

    class HostAdapter extends BaseAdapter {

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
            Host host = servers.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.serverentry, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tvHostname = (TextView) convertView.findViewById(R.id.hostname);
                viewHolder.ivTrafficLight = (ImageView) convertView.findViewById(R.id.trafficLight);
                viewHolder.tvState = (TextView) convertView.findViewById(R.id.status);
                viewHolder.tvLastCheck = (TextView) convertView.findViewById(R.id.lastCheck);
                viewHolder.tvDuration = (TextView) convertView.findViewById(R.id.duration);
//                viewHolder.serviceAdapter = new ServiceAdapter();
//                viewHolder.lvServices = (ListView) convertView.findViewById(R.id.servicesListView);
//                viewHolder.lvServices.setAdapter(viewHolder.serviceAdapter);
                convertView.setTag(viewHolder);
            }
            if (position % 2 == 0) {
                convertView.setBackgroundColor(Color.parseColor("#fffae8"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#fedcbb"));
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tvHostname.setText(host.name);
            viewHolder.tvState.setText(host.status);
            viewHolder.tvLastCheck.setText(host.lastCheck);
            viewHolder.tvDuration.setText(host.duration);
//            viewHolder.serviceAdapter.setServices(host.services);
//            viewHolder.lvServices.setAdapter(viewHolder.serviceAdapter);
//            viewHolder.serviceAdapter.notifyDataSetChanged();
            return convertView;
        }
    }

    class ServiceAdapter extends BaseAdapter {
        private List<Service> services = new ArrayList<>();

        @Override
        public int getCount() {
            return services.size();
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
            Service service = services.get(position);
            if (service == null) {
                return null;
            }

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.serviceentry, null);
            }
            Log.d(TAG, service.toString());
            ((TextView) convertView.findViewById(R.id.serviceName)).setText(service.service);
            return convertView;
        }

        public void setServices(List<Service> services) {
            this.services = services;
        }
    }

    class ViewHolder {
        TextView tvHostname;
        ImageView ivTrafficLight;
        TextView tvState;
        TextView tvLastCheck;
        TextView tvDuration;

//        ListView lvServices;
//        ServiceAdapter serviceAdapter;
    }
}
