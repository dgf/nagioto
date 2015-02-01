package de.g2d.nagioto.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.g2d.nagioto.MainActivity;
import de.g2d.nagioto.R;

/**
 * Created by sasse_h on 01.02.15.
 */
public class Info extends Fragment implements View.OnClickListener {
    public final static String TAG = Info.class.getSimpleName();
    private Throwable throwable;
    private String label;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.simple_info, null);
        TextView tvLabel = (TextView) view.findViewById(R.id.label);
        tvLabel.setText(label != null ? label : tvLabel.getText());
        if (throwable != null) {
            ((TextView) view.findViewById(R.id.info)).setText(throwable.getLocalizedMessage());
            if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
                view.findViewById(R.id.stacktrace_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.stacktrace_btn).setOnClickListener(this);
            }
        }
        if (label.equals(getResources().getString(R.string.error_network_label))) {
            view.findViewById(R.id.conn_btn).setVisibility(View.VISIBLE);
            view.findViewById(R.id.conn_btn).setOnClickListener(this);
        }
        return view;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void onClick(View v) {
        View parent = getView();
        if (parent != null) {
            switch (v.getId()) {
                case R.id.stacktrace_btn:
                    parent.findViewById(R.id.stacktrace_container).setVisibility(View.VISIBLE);
                    TextView tvStacktraceOverlay = (TextView) parent.findViewById(R.id.stacktrace);
                    StringBuilder sb = new StringBuilder();
                    if (throwable != null) {
                        for (StackTraceElement element : throwable.getStackTrace()) {
                            sb.append(element.toString());
                            sb.append("\n");
                        }
                        tvStacktraceOverlay.setText(sb.toString());
                    }
                    break;
                case R.id.conn_btn:
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE);
                    break;
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
