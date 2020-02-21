package com.bil24.fragments;

import android.graphics.Color;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v4.app.*;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.myelement.MyDialogFragment;
import com.bil24.utils.Utils;
import com.squareup.picasso.Picasso;
import server.net.obj.extra.ExtraVenue;

import java.util.regex.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class CityPassVenueFragment extends MyDialogFragment {
  private static String ARGUMENT_VENUE = "ARGUMENT_VENUE";
  private static final Pattern pattern = Pattern.compile("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");

  @BindView(R.id.venueCityPassFragmentContentPanel)
  RelativeLayout content;
  @BindView(R.id.venueCityPassFragmentDescription)
  TextView tvDescription;
  @BindView(R.id.venueCityPassFragmentVenuePoster)
  ImageView poster;
  @BindView(R.id.venueCityPassFragmentTvMap)
  TextView tvMap;
  @BindView(R.id.venueCityPassFragmentWebView)
  WebView webView;
  private Unbinder unbinder;

  private ExtraVenue venue;
  private boolean smallLayout = false;

  public static MyDialogFragment newInstance(@NonNull ExtraVenue venue) {
    CityPassVenueFragment fragment = new CityPassVenueFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ARGUMENT_VENUE, venue);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View resultView = inflater.inflate(R.layout.fragment_venue_city_pass, container, false);
    unbinder = ButterKnife.bind(this, resultView);

    venue = (ExtraVenue) getArguments().getSerializable(ARGUMENT_VENUE);
    if (venue == null) return resultView;

    smallLayout = content.getTag().equals("small");
    tvMap.setVisibility(View.GONE);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setBackgroundColor(Color.WHITE);

    StringBuffer sb = new StringBuffer();
    Matcher matcher = pattern.matcher(venue.getDescription());
    while (matcher.find()) {
      String url = matcher.group();
      matcher.appendReplacement(sb, "<a href=\"" + url + "\">" + url + "</a>");
    }
    matcher.appendTail(sb);
    String result = sb.toString();
    result = result.replaceAll("\\n", "<br>");

    tvDescription.setText(Html.fromHtml(result));
    tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
    tvDescription.setLinkTextColor(getContext().getResources().getColor(R.color.toolbar));
    Picasso.with(getContext())
        .load(venue.getImageUrl())
        .placeholder(R.drawable.poster1)
        .error(R.drawable.poster1).fit()
        .into(poster);

    if (!smallLayout) {
      content.getViewTreeObserver().addOnGlobalLayoutListener(new ScrollObserver());
    } else {
      webView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageFinished(final WebView view, String url) {
          tvMap.setVisibility(View.VISIBLE);
          tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ScrollView scrollView = resultView.findViewById(R.id.venueCityPassFragmentScrollPanel);
              scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
          });
          super.onPageFinished(view, url);
        }
      });
    }
    return resultView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  //Почему засунул в этот метод: Когда webview находится в конце лаяута, оно не имеет размеров. Хз почему
  @Override
  public void show(FragmentManager manager, String tag) {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (smallLayout) {
          ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
          layoutParams.height = poster.getMeasuredHeight();
          layoutParams.width = poster.getMeasuredWidth();
          webView.setLayoutParams(layoutParams);
        }
        String mapsHtml = Utils.readFileFromAssets(getActivity(), "yandex.html");
        mapsHtml = mapsHtml.replace("#LAT", venue.getGeoLat());
        mapsHtml = mapsHtml.replace("#LNG", venue.getGeoLon());
        mapsHtml = mapsHtml.replace("#VENUENAME", venue.getVenueName());
        mapsHtml = mapsHtml.replace("#VENUEADDRESS", venue.getAddress());
        webView.loadDataWithBaseURL("file:///android_asset/", mapsHtml, "text/html", "UTF-8", null);
      }
    }, 100);
    super.show(manager, tag);
  }

  private class ScrollObserver implements ViewTreeObserver.OnGlobalLayoutListener {

    @Override
    public void onGlobalLayout() {
      content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      View inner = content.getChildAt(0);
      content.removeViewAt(0);
      ScrollView scrollView = new ScrollView(getContext());
      scrollView.addView(inner);
      content.addView(scrollView);
    }
  }
}