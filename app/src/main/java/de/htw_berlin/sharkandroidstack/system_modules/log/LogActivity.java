package de.htw_berlin.sharkandroidstack.system_modules.log;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;

/*
* open certain log on activity start by intent extra:
* intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
* */
public class LogActivity extends ParentActivity {

    public static final String OPEN_LOG_ID_ON_START = "activity_log_open_log_id_on_start";
    Spinner spinner;
    ArrayAdapter logAdapter;

    public final AdapterView.OnItemSelectedListener itemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String readableName = ((TextView) view).getText().toString();
            String name = LogManager.findLogIdByName(readableName);

            final List<LogManager.LogEntry> allEntries = LogManager.getAllEntries(name);
            logAdapter.clear();
            logAdapter.addAll(allEntries);
            logAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public final LogManager.LogChangeListener logChangeLister = new LogManager.LogChangeListener() {
        @Override
        public void update(String name, LogManager.LogEntry entry) {
            logAdapter.add(entry);
            logAdapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int nextItem = (spinner.getSelectedItemPosition() + 1) % spinner.getCount();
            spinner.setSelection(nextItem);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutResource(R.layout.system_module_log_activity);

        initSpinner();
        initListener();
        initLogViewAndAdapter();

        final Button nextButton = (Button) findViewById(R.id.activity_log_next_button);
        nextButton.setOnClickListener(nextButtonClickListener);
    }

    private void initSpinner() {
        final List<String> allNames = LogManager.getAllNames();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allNames);
        spinner = (Spinner) findViewById(R.id.activity_log_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemClickListener);
    }

    private void initListener() {
        final List<String> all = LogManager.getAllLogIds();
        for (String a : all) {
            LogManager.addListener(logChangeLister, a);
        }
    }

    private void initLogViewAndAdapter() {
        logAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                final TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                LogManager.LogEntry item = (LogManager.LogEntry) this.getItem(position);
                text1.setText("Priority: " + item.prio);
                text2.setText(item.msg);

                return view;
            }
        };

        final ListView log = (ListView) findViewById(R.id.activity_log_text);
        log.setAdapter(logAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String initialLog = getIntent().getStringExtra(OPEN_LOG_ID_ON_START);
        if(initialLog != null) {
            int index = LogManager.getAllLogIds().indexOf(initialLog);
            if(index != -1) {
                spinner.setSelection(index);
            }
        }
    }

    //TODO: implement kb log
}
