package com.bil24.dialog;

import android.os.Bundle;
import android.view.*;
import android.widget.GridView;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.adapter.mec.MECsQrAdapter;
import com.bil24.myelement.MyDialogFragment;
import server.net.obj.extra.ExtraMEC;

import java.util.ArrayList;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
@SuppressWarnings("unchecked")
public class MECsQrDialogFragment extends MyDialogFragment {
  private static final String ACTION_NAME = "ACTION_NAME";
  private static final String TICKET = "TICKET";

  @BindView(R.id.mecsQrFragmentGridView)
  GridView gridView;

  public static MECsQrDialogFragment create(ArrayList<ExtraMEC> list, String actionName) {
    MECsQrDialogFragment ticketDialogFragment = new MECsQrDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(TICKET, list);
    bundle.putString(ACTION_NAME, actionName);
    ticketDialogFragment.setArguments(bundle);
    return ticketDialogFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_fragment_mecs_qr, container, false);
    ButterKnife.bind(this, view);

    try {
      ArrayList<ExtraMEC> list = (ArrayList<ExtraMEC>) getArguments().getSerializable(TICKET);
      String actionName = getArguments().getString(ACTION_NAME);
      getDialog().setTitle(actionName);
      gridView.setAdapter(new MECsQrAdapter(getActivity(), list));
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }
    return view;
  }

}