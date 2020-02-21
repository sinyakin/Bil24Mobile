package com.bil24.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import butterknife.*;
import com.bil24.R;

public class InFragment extends Fragment {
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_in, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  public void showFragment(final Fragment fragment, final String id) {

    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        String backStateName = fragment.getClass().getName() + "_" + id;
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.inContainer, fragment, backStateName);
        transaction.addToBackStack(backStateName);
        transaction.commit();
      }
    }, 100);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }
}
