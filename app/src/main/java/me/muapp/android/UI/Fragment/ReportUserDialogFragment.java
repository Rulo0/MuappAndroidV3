package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnUserReportedListener;

/**
 * Created by rulo on 9/05/17.
 */

public class ReportUserDialogFragment extends DialogFragment implements View.OnClickListener {
    MatchingUser matchingUser;
    static final String ARG_MATCHING_USER = "ARG_MATCHING_USER";
    OnUserReportedListener onUserReportedListener;
    Button btn_cancel_report, btn_report_user;
    ListView list_report_reasons;
    int selectedReason;

    public ReportUserDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            matchingUser = getArguments().getParcelable(ARG_MATCHING_USER);
        }
    }

    public void setOnUserReportedListener(OnUserReportedListener onUserReportedListener) {
        this.onUserReportedListener = onUserReportedListener;
    }

    public static ReportUserDialogFragment newInstance(MatchingUser user) {
        ReportUserDialogFragment fragment = new ReportUserDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MATCHING_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_report_user, container, false);
        btn_cancel_report = (Button) v.findViewById(R.id.btn_cancel_report);
        btn_report_user = (Button) v.findViewById(R.id.btn_report_user);
        list_report_reasons = (ListView) v.findViewById(R.id.list_report_reasons);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_cancel_report.setOnClickListener(this);
        btn_report_user.setOnClickListener(this);
        String[] values = new String[]{
                getContext().getString(R.string.lbl_report_reason_photos),
                getContext().getString(R.string.lbl_report_reason_comments),
                getContext().getString(R.string.lbl_report_reason_fake),
                getContext().getString(R.string.lbl_report_reason_other)
        };
        btn_report_user.setEnabled(false);
        btn_report_user.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.color_inactive_tab), PorterDuff.Mode.MULTIPLY);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.report_item_layout, R.id.txt_report_reason, values);
        list_report_reasons.setAdapter(adapter);
        list_report_reasons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedReason = position;
                for (int i = 0; i < list_report_reasons.getCount(); i++) {
                    View v = list_report_reasons.getChildAt(i);
                    TextView txt_report_reason = (TextView) v.findViewById(R.id.txt_report_reason);
                    txt_report_reason.setTextColor(i == position ? ContextCompat.getColor(getContext(), R.color.colorAccent) : ContextCompat.getColor(getContext(), R.color.color_muapp_dark));
                }
                if (!btn_report_user.isEnabled()) {
                    btn_report_user.setEnabled(true);
                    btn_report_user.getBackground().clearColorFilter();
                }
            }
        });
    }

    private void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report_user:
                new APIService(getContext()).reportUser(matchingUser.getId(), selectedReason, null);
                if (onUserReportedListener != null)
                    onUserReportedListener.onReport();
            case R.id.btn_cancel_report:
                dismissDialog();
                break;
        }
    }
}
