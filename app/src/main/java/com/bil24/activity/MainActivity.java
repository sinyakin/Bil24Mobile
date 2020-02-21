package com.bil24.activity;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bil24.*;
import com.bil24.activity.bank.BankActivity;
import com.bil24.activity.seatingplan.SeatingPlanActivity;
import com.bil24.adapter.MyFragmentPagerAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.fragments.*;
import com.bil24.menu.*;
import com.bil24.net.AndroidSender;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import server.net.*;
import server.net.listener.AuthListener;
import server.net.obj.AuthClient;
import server.net.obj.extra.*;

public class MainActivity extends BaseActivity implements MainActivityListener, AuthListener, UpdateTopMenuListener, SecondFragment.MenuHideListener {
  public static final String EXTRA_SHOW_NEWS = "EXTRA_SHOW_NEWS";

  private final int REQUEST_CODE_ALFA = 1;
  private final int REQUEST_CODE_SEATING_PLAN = 2;
  private final int REQUEST_CODE_AUTH = 3;
  private MyDrawer myDrawer = null;

  private ViewPager pager;

  private FrameLayout frameLayout;
  private Toolbar toolbar;
  private BasketFragment basketFragment;

  private OrderFragment orderFragment;
  private Ticket1Fragment ticket1Fragment;
  private EmptyFragment emptyFragment;
  private SettingFragment settingFragment;
  private AgreementFragment agreementFragment;
  private NewsFragment newsFragment;
  private Mec1Fragment mecsFragment;
  private PromoCodesFragment promoCodesFragment;
  private Drawable defaultIconToolbar;

  private boolean doubleBackToExitPressedOnce = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Bil24Application.withMiniDrawer()) setContentView(R.layout.activity_main_v2_mini_drawer);
    else setContentView(R.layout.activity_main_v2);

    Controller.getInstance().setMainActivityListener(this);
    Controller.getInstance().setUpdateTopMenuListener(this);
    //инициализация сетевого интерфейса
    NetManager.init(new AndroidSender());

    // Инициализируем Toolbar
    if (toolbar == null) toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

    pager = (ViewPager) findViewById(R.id.pager);
    pager.setVisibility(View.GONE);
    MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext());
    pagerAdapter.setPager(pager);
    pager.setAdapter(pagerAdapter);

    frameLayout = (FrameLayout) findViewById(R.id.container);

    auth();
/*
    Display display = getWindowManager().getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);
    Integer height;
    Integer width;
    if (metrics.widthPixels < metrics.heightPixels) {
      width = metrics.widthPixels;
      height = metrics.heightPixels;
    } else {
      width = metrics.heightPixels;
      height = metrics.widthPixels;
    }
    Log.d("!!!!!", "width " + width + " height " + height + " " + metrics.density + " " + metrics.densityDpi + " " + metrics.scaledDensity);
*/
  }

  private void auth() {
    Session session = DataBase.getSession(this);
    Long userId = session.getUserId();
    String sessionId = session.getSessionId();
    if (!session.isAuth()) {
      userId = null;
      sessionId = null;
    }
    NetManager.auth(this, userId, sessionId);
  }

  @Override
  public void onBackPressed() {
    //если открыт фрагмент второго уровня, то скроем его
    if (currentFragment != null && currentFragment instanceof SecondFragment) {
      getSupportFragmentManager().popBackStack();
      if (oldActionBarTitle != null) setTitle(oldActionBarTitle);
      currentFragment = null;
      return;
    }
    // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
    if (myDrawer.isDrawerOpen()) {
      myDrawer.closeDrawer();
    } else if (pager.getVisibility() != View.VISIBLE) {
      myDrawer.setSelection(LeftMenu.ACTIONS);
    } else if (Controller.getInstance().getPage() == 1) {
      Controller.getInstance().openPage1(false);
    } else {
      if (doubleBackToExitPressedOnce) {
        super.onBackPressed();
        finish();
      }
      this.doubleBackToExitPressedOnce = true;
      Toast.makeText(this, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
      new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {
          doubleBackToExitPressedOnce = false;
        }
      }, 2000);
    }
  }

  private Menu menu;
  private MenuItem ticketMenu;
  private TextView ticketMenuTv;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    this.menu = menu;
    ticketMenu = menu.findItem(R.id.action_tickets);
    ticketMenu.setActionView(R.layout.menu_badge);

    View ticketView = ticketMenu.getActionView();
    ImageView ticketIV = (ImageView) ticketView.findViewById(R.id.menuIV);
    ticketIV.setImageResource(R.mipmap.ic_menu_ticket);
    ticketMenuTv = (TextView) ticketView.findViewById(R.id.menuCounter);

    ticketView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onOptionsItemSelected(ticketMenu);
      }
    });

    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (checkingSearchTextByKeyWord(query)) return true;
        if (myDrawer.drawer.getCurrentSelection() != LeftMenu.ACTIONS.ordinal()) {
          myDrawer.setSelection(LeftMenu.ACTIONS);
        } else if (Controller.getInstance().getPage() == 1) {
          Controller.getInstance().openPage1(false);
        }

        Controller.getInstance().actionFilter(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) Controller.getInstance().setFilterText("");
        return checkingSearchTextByKeyWord(newText);
      }
    });
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        Controller.getInstance().actionFilter("");
        return false;
      }
    });

    Controller.getInstance().updateLeftMenuTicketAmount();
    Controller.getInstance().updateLeftMenuMECsAmount();
    Controller.getInstance().updateLeftMenuBasketAmount(Settings.getSeatInReserve());
    Controller.getInstance().updateLeftMenuOrderAmount(Settings.getWaitInReserve());
    Controller.getInstance().updateLeftMenuPromoCodesAmount(Settings.getPromoCodes());
    return true;
  }

  private boolean checkingSearchTextByKeyWord(String text) {
    if (text.equalsIgnoreCase("qwerty10")) {
      Dialogs.showAdminDialog(getSupportFragmentManager());
      return true;
    } else if (SettingsCommon.isRealZone() && text.equalsIgnoreCase("qazwsx800")) {
      Dialogs.showRmkDialog(getSupportFragmentManager());
      return true;
    }
    return false;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.action_tickets:
        myDrawer.setSelection(LeftMenu.MY_TICKETS);
        return true;
      case R.id.action_search:
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void updateTicketTopMenu(String text) {
    if (ticketMenuTv == null) return;
    if (text.isEmpty()) ticketMenuTv.setVisibility(View.GONE);
    else ticketMenuTv.setVisibility(View.VISIBLE);
    ticketMenuTv.setText(text);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (Settings.isNeedAuth(this)) {
      Intent intent = new Intent(this, AuthActivity.class);
      startActivityForResult(intent, REQUEST_CODE_AUTH);
    }

    if (toolbar == null) toolbar = (Toolbar) findViewById(R.id.toolbar);
    boolean showNews = getIntent().getBooleanExtra(EXTRA_SHOW_NEWS, false);
    // Инициализируем Navigation Drawer
    if (myDrawer == null) {
      myDrawer = new MyDrawer(this, toolbar);
      if (!showNews) myDrawer.setSelection(LeftMenu.ACTIONS);
    }
    if (showNews) {
      showNewsFragment();
      //удалим все пуши
      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancelAll();
      getIntent().putExtra(EXTRA_SHOW_NEWS, false);
    }
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (Bil24Application.withMiniDrawer() && view instanceof AppCompatImageButton) onBackPressed();
        else {
          //если был открыт фрагмент второго уровня
          if (currentFragment != null && currentFragment instanceof SecondFragment) onBackPressed();
          else myDrawer.drawerOpen();
        }
      }
    });
    defaultIconToolbar = toolbar.getNavigationIcon();
  }

  @Override
  public void onPush() {
    if (newsFragment == null) return;
    newsFragment.onPush();
  }

  @Override
  public void onNewIntent(Intent newIntent) {
    //решает проблему: когда запущена активность, то
    //по нажатию на пуш extra не передаются
    this.setIntent(newIntent);
  }

  @Override
  public void showFragment(Fragment fragment) {
    pager.setVisibility(View.GONE);
    frameLayout.setVisibility(View.VISIBLE);
    super.showFragment(fragment);
  }

  @Override
  public void showActionPageFragment() {
    toolbar.setTitle(" " + Controller.getInstance().getFrontendData(this).getFrontendType().getDesc());
    toolbar.setLogo(R.mipmap.logo_toolbar);
    myDrawer.setSelection(LeftMenu.ACTIONS, false);
    frameLayout.setVisibility(View.GONE);
    pager.setVisibility(View.VISIBLE);
    pager.setCurrentItem(0);
    removeFragment();
  }

  @Override
  public void showBasketFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.BASKET);
    if (basketFragment == null) basketFragment = new BasketFragment();
    showFragment(basketFragment);
  }

  @Override
  public void showOrderFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.MY_ORDERS);
    if (orderFragment == null) orderFragment = new OrderFragment();
    showFragment(orderFragment);
  }

  @Override
  public void showTicket1Fragment(int tryingGetTickets) {
    showMenuWithoutLogoFirstFragment(LeftMenu.MY_TICKETS);
    if (ticket1Fragment == null) ticket1Fragment = new Ticket1Fragment();
    ticket1Fragment.setQuantityRequests(tryingGetTickets);
    showFragment(ticket1Fragment);
  }

  @Override
  public void showTicket2Fragment(long actionEventId, String actionName) {
    showFragment(Ticket2Fragment.newInstance(actionEventId, actionName));
  }

  @Override
  public void showMec1Fragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.MY_MECS);
    if (mecsFragment == null) mecsFragment = new Mec1Fragment();
    showFragment(mecsFragment);
  }

  @Override
  public void showMec2Fragment(long actionEventId, long cityId, String actionName, String venueName, boolean cityPass) {
    showFragment(Mec2Fragment.newInstance(actionEventId, cityId, actionName, venueName, cityPass));
  }

  @Override
  public void showPromoCodesFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.MY_PROMO_CODES);
    if (promoCodesFragment == null) promoCodesFragment = new PromoCodesFragment();
    showFragment(promoCodesFragment);
  }

  @Override
  public void showSettingFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.SETTING);
    if (settingFragment == null) settingFragment = new SettingFragment();
    showFragment(settingFragment);
  }

  @Override
  public void showAgreementFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.AGREEMENT);
    if (agreementFragment == null) agreementFragment = new AgreementFragment();
    showFragment(agreementFragment);
  }

  @Override
  public void showCityPassFragment(long cityId) {
    CityPassFragment.newInstance(cityId).show(getSupportFragmentManager(), null);
  }

  @Override
  public void showCityPassVenueFragment(@NonNull ExtraVenue venue) {
    CityPassVenueFragment.newInstance(venue).show(getSupportFragmentManager(), null);
  }

  @Override
  public void showEmptyFragment() {
    if (emptyFragment == null) emptyFragment = new EmptyFragment();
    showFragment(emptyFragment);
  }

  @Override
  public void showNewsFragment() {
    showMenuWithoutLogoFirstFragment(LeftMenu.NEWS);
    if (newsFragment == null) newsFragment = new NewsFragment();
    showFragment(newsFragment);
  }

  private void showMenuWithoutLogoFirstFragment(LeftMenu leftMenu) {
    myDrawer.setSelection(leftMenu, false);
    toolbar.setTitle(leftMenu.getDesc());
    toolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    oldActionBarTitle = toolbar.getTitle();
  }

  @Override
  public void showAlfaActivity(Long orderId, String url, boolean openMECFragmentAfterPaid) {
    Intent intent = BankActivity.getStartIntent(this, orderId, url, openMECFragmentAfterPaid);
    startActivityForResult(intent, REQUEST_CODE_ALFA);
  }

  @Deprecated
  @Override
  public void showTicketActivity(long actionEventId, String actionName) {
    Intent intent = TicketsActivity.getStartIntent(this, actionEventId, actionName);
    startActivity(intent);
  }

  @Deprecated
  @Override
  public void showMECActivity(long actionEventId, String actionName, String venueName) {
    Intent intent = MECsActivity.getStartIntent(this, actionEventId, actionName, venueName);
    startActivity(intent);
  }

  @Override
  public void showSeatingPlanActivity(String placementUrl, long actionId, String actionName, Long actionEventId, String actionEventTime) {
    Intent intent = SeatingPlanActivity.getStartIntent(this, placementUrl, actionId, actionName, actionEventId, actionEventTime);
    startActivityForResult(intent, REQUEST_CODE_SEATING_PLAN);
  }

  @Override
  public void showImageActivity(String url) {
    Intent intent = ImageActivity.getStartIntent(this, url);
    startActivity(intent);
  }

  @Override
  public void showCityActivity() {
    Intent intent = CityActivity.getStartIntent(this);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_ALFA) {
      switch (resultCode) {
        case BankActivity.RESULT_OK: {
          showTicket1Fragment(3);
          Dialogs.showSnackBar(this, "Оплата прошла успешно");
          break;
        }
        case BankActivity.RESULT_OK_MEC: {
          showMec1Fragment();
          Dialogs.showSnackBar(this, "Оплата прошла успешно");
          break;
        }
        case BankActivity.RESULT_ERROR: {
          Dialogs.alert(this, "Платеж не выполнен. Списание средств не произведено.\r\n" +
              "Sorry, your payment failed. No charges were made.");
          break;
        }
        case BankActivity.RESULT_CANCELED: {
          showOrderFragment();
          final long orderId = data.getLongExtra(BankActivity.EXTRA_ORDER_ID, 0);
          new Handler().post(new Runnable() {
            @Override
            public void run() {
              Dialogs.showInfoDialog(getSupportFragmentManager(), "Внимание", "Заказ № " + orderId + " доступен в разделе «Мои заказы», где его можно оплатить в течение 10 минут");
            }
          });
          break;
        }
      }
    } else if (requestCode == REQUEST_CODE_SEATING_PLAN) {
      if (resultCode == SeatingPlanActivity.RESULT_OPEN_BASKET) showBasketFragment();
    } else if (requestCode == REQUEST_CODE_AUTH) {
      if (resultCode == AuthActivity.RESULT_CANCELED) finish();
    }
  }

  @Override
  public void onAuth(AuthClient clientData) {
    DataBase.getInstance(this).mainData.updateSession(clientData.getUserId(), clientData.getSessionId());
    ExtraUpdate extraUpdate = clientData.getExtraUpdate();
    if (extraUpdate == null) {
      if (Controller.getInstance().changeFrontendTypeInKassatka(this)) {
        Dialogs.showRmkDialog(getSupportFragmentManager());
      }
      return;
    }
    Dialogs.showUpdateDialog(getSupportFragmentManager(), extraUpdate.getInfo(), extraUpdate.getUrl(), extraUpdate.isOnlyUpdate());
  }

  @Override
  public void onAuthFailed(NetException e) {
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        auth();
      }
    }, 5000);
  }

  @Override
  public void onShowMenu() {
    if (menu == null) return;
    if (defaultIconToolbar != null) toolbar.setNavigationIcon(defaultIconToolbar);
    for (int i = 0; i < menu.size(); i++) menu.getItem(i).setVisible(true);
  }

  @Override
  public void onHideMenu() {
    if (menu == null) return;
    for (int i = 0; i < menu.size(); i++) menu.getItem(i).setVisible(false);
  }
}