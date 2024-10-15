package com.example.stepappv4.ui.report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentReportBinding;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    public int todaySteps = 0;
    public MaterialButton button_get;
    public MaterialButton button_today;
    public MaterialButton button_delete;
    public TextView completedStepsText;
    public StepAppOpenHelper stepAppOpenHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReportViewModel galleryViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);

        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Timestamp
        long timeInMillis = System.currentTimeMillis();
        // Convert the timestamp to date
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        final String dateTimestamp = jdf.format(timeInMillis);
        String currentDay = dateTimestamp.substring(0,10);


        stepAppOpenHelper = new StepAppOpenHelper(getContext());
        completedStepsText = (TextView) root.findViewById(R.id.numCompletedSteps);

        // GET ENTRIES button
        button_get = (MaterialButton) root.findViewById(R.id.button_get);
        button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepAppOpenHelper.loadRecords(getContext());

            }
        });


        // TODAY button
        button_today = (MaterialButton) root.findViewById(R.id.button_today);
        button_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfSteps = stepAppOpenHelper.loadSingleRecord(getContext(), currentDay);
                Log.d("RETRIEVED STEPS TODAY: ", String.valueOf(numberOfSteps));
                completedStepsText.setText(String.valueOf(numberOfSteps));
            }
        });

        // DELETE button
        button_delete = (MaterialButton) root.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepAppOpenHelper.deleteRecords(getContext());
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}