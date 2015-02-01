package de.g2d.nagioto.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.g2d.nagioto.R;

/**
 * Created by sasse_h on 01.02.15.
 */
public class Info extends Fragment implements View.OnClickListener {
    public final static String TAG = Info.class.getSimpleName();
    private Throwable throwable;
    private boolean stacktraceVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.simple_info, null);
        if (throwable != null) {
            TextView tvContent = (TextView) view.findViewById(R.id.info);
            tvContent.setText(throwable.getLocalizedMessage());
            view.findViewById(R.id.stacktrace_btn).setOnClickListener(this);
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
                    stacktraceVisible = true;
                    break;
            }
        }
    }

    public boolean isStacktraceVisible() {
        return stacktraceVisible;
    }

    public void setStacktraceVisible(boolean stacktraceVisible) {
        if (!stacktraceVisible && getView() != null) {
            getView().findViewById(R.id.stacktrace_container).setVisibility(View.INVISIBLE);
        }
        this.stacktraceVisible = stacktraceVisible;
    }
}
