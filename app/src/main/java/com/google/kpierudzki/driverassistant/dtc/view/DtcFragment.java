package com.google.kpierudzki.driverassistant.dtc.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.kpierudzki.driverassistant.MainActivityFragmentsCallbacks;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.dtc.DtcContract;
import com.google.kpierudzki.driverassistant.dtc.DtcPresenter;
import com.google.kpierudzki.driverassistant.dtc.database.DtcEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcFragment extends Fragment implements DtcContract.View {

    public final static String TAG = "DtcFragment_TAG";

    @BindView(R.id.dtc_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.dtc_no_dtc_label)
    View noDataLabel;

    private MainActivityFragmentsCallbacks mainActivityFragmentsCallbacks;
    private DtcAdapter adapter;
    private DtcContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dtc, container, false);
        ButterKnife.bind(this, root);

        if (mainActivityFragmentsCallbacks != null)
            mainActivityFragmentsCallbacks.onFragmentLoaded
                    (MainActivityFragmentsCallbacks.LoadedFragment.Dtc);

        prepareAppBar(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DtcAdapter();
        recyclerView.setAdapter(adapter);

        new DtcPresenter(this);

        return root;
    }

    public static DtcFragment newInstance() {
        return new DtcFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prepareAppBar(false);
        presenter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) presenter.stop();
    }

    @Override
    public void setPresenter(DtcContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPresenterReady(DtcContract.Presenter presenter) {
        presenter.fetchData();
    }

    @Override
    public void fillList(List<DtcEntity> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        if (data.isEmpty()) {
            noDataLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showClearDtcIcon(boolean show) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                //Remove previous icon
                supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

                if (show) {
                    AppCompatImageView icon = new AppCompatImageView(activity);
                    icon.setImageResource(R.drawable.ic_clear_all);
                    icon.setOnClickListener(view -> {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.Dtc_ConfirmDialog_Message)
                                .setNegativeButton(R.string.Dtc_ConfirmDialog_Negative,
                                        (dialogInterface, i) -> dialogInterface.dismiss())
                                .setPositiveButton(R.string.Dtc_ConfirmDialog_Positive,
                                        (dialogInterface, i) -> {
                                            if (presenter != null) presenter.clearAllDtc();
                                            dialogInterface.dismiss();
                                        })
                                .create().show();
                    });
                    ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END;
                    params.setMargins((int) activity.getResources().getDimension(R.dimen.Dtc_Toolbar_ClearDtcIcon_Margin),
                            0,
                            (int) activity.getResources().getDimension(R.dimen.Dtc_Toolbar_ClearDtcIcon_Margin),
                            0);

                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
                            ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                    supportActionBar.setCustomView(icon, params);
                }
            }
        }
    }

    private void prepareAppBar(boolean initialize) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (initialize) {
                    supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                    supportActionBar.setTitle(R.string.Dtc_Name);
                } else {
                    supportActionBar.setDisplayOptions(0);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdReadFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mainActivityFragmentsCallbacks = (MainActivityFragmentsCallbacks) getActivity();
        } catch (Exception e) {
            Log.e("ObdReadFragment", e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivityFragmentsCallbacks = null;
    }
}
