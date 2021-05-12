package modals;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sqube.desantosdirectory.R;

import interfaces.OnStatusChangeListener;

import static models.Commons.STATUS;

public class FilterFragment extends BottomSheetDialogFragment {
    private EditText edtStatus;
    private Spinner spnStatus;
    private int status;
    private boolean justOpened = true;
    private OnStatusChangeListener listener;
    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getArguments().getInt(STATUS);
        listener = (OnStatusChangeListener) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filter, container, false);
        edtStatus = view.findViewById(R.id.edtStatus);
        switch (status){
            case 0:
                edtStatus.setText("Pending");
                break;
            case 1:
                edtStatus.setText("Successful");
                break;
            case 2:
                edtStatus.setText("Cancelled");
                break;
            case 3:
                edtStatus.setText("All statuses");
                break;
        }
        edtStatus.setOnClickListener(v -> {
            justOpened = false;
            spnStatus.performClick();
        });
        Button btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> listener.onStatusChange(status));
        spnStatus = view.findViewById(R.id.spnStatus);
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(justOpened)
                    return;
                switch (position){
                    case 0:
                        edtStatus.setText("All statuses");
                        status = 3;
                        break;
                    case 1:
                        edtStatus.setText("Successful");
                        status = 1;
                        break;
                    case 2:
                        edtStatus.setText("Pending");
                        status = 0;
                        break;
                    case 3:
                        edtStatus.setText("Cancelled");
                        status = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }
}