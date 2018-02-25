package com.google.kpierudzki.driverassistant.debug.export_main_db;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.service.database.AssistantDatabase;
import com.google.kpierudzki.driverassistant.util.DatabaseExtractorUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kamil on 16.07.2017.
 */

public class DebugDrawerExportDb implements IDestroyable, View.OnClickListener {

    @BindView(R.id.label)
    TextView label;

    public DebugDrawerExportDb(View view) {
        View root = view.findViewById(R.id.dbg_navexport_db);
        ButterKnife.bind(this, root);
        root.setOnClickListener(this);
        label.setText("Export DB");
    }

    @Override
    public void onDestroy() {
        //..
    }

    @Override
    public void onClick(View v) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
            DatabaseExtractorUtil.extractDB(AssistantDatabase.DATABASE_FILENAME,
                    AssistantDatabase.DATABASE_FILENAME + df.format(Calendar.getInstance().getTime()));
            Toast.makeText(App.getAppContext(), "Export success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(App.getAppContext(), "Export failed", Toast.LENGTH_SHORT).show();
            Log.e("DebugDrawerExportDb", e.getLocalizedMessage());
        }
    }
}
