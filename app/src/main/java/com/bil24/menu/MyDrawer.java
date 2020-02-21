package com.bil24.menu;

import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.activity.*;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.holder.*;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.mikepenz.materialize.util.UIUtils;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class MyDrawer implements UpdateLeftMenuListener {

  public Drawer drawer;
  private MiniDrawer miniDrawer;
  private Crossfader crossFader;

  public MyDrawer(final MainActivity activity, final Toolbar toolbar) {
    IDrawerItem[] drawersItem = new IDrawerItem[LeftMenu.values().length];
    int i = 0;
    for (LeftMenu leftMenu : LeftMenu.values()) {
      drawersItem[i++] = leftMenu.getiDrawerItem();
    }
    DrawerBuilder drawerBuilder = new DrawerBuilder();
    drawerBuilder.withActivity(activity);
    drawerBuilder.withDrawerWidthDp(300);
    drawerBuilder.withToolbar(toolbar);
    drawerBuilder.withActionBarDrawerToggle(true);
    drawerBuilder.withActionBarDrawerToggleAnimated(true);
    drawerBuilder.withFullscreen(true);
    drawerBuilder.addDrawerItems(drawersItem);
    drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
      @Override
      public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
        if (drawerItem instanceof Nameable) {
          final String name = ((Nameable) drawerItem).getName().getText();

          final LeftMenu leftMenu = LeftMenu.get((drawerItem.getIdentifier()));
          if (leftMenu == null) return true;
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              switch (leftMenu) {
                case ACTIONS:
                  Controller.getInstance().showActionPageFragment();
                  break;
                case BASKET:
                  Controller.getInstance().showBasketFragment();
                  break;
                case MY_ORDERS:
                  Controller.getInstance().showOrderFragment();
                  break;
                case MY_TICKETS:
                  Controller.getInstance().showTicket1Fragment(1);
                  break;
                case MY_MECS:
                  Controller.getInstance().showMec1Fragment();
                  break;
                case MY_PROMO_CODES:
                  Controller.getInstance().showPromoCodesFragment();
                  break;
                case NEWS:
                  Controller.getInstance().showNewsFragment();
                  break;
                case SETTING:
                  Controller.getInstance().showSettingFragment();
                  break;
                case AGREEMENT:
                  Controller.getInstance().showAgreementFragment();
                  break;
                default:
                  toolbar.setTitle(name);
                  toolbar.setLogo(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                  Controller.getInstance().showEmptyFragment();
                  break;
              }
              //без этого при нажатии на back button адаптер не обновлял элементы
              drawer.getAdapter().notifyAdapterDataSetChanged();
            }
          }, 150);
          //без этого при нажатии на back button адаптер не обновлял элементы
          drawer.getAdapter().notifyAdapterDataSetChanged();
        }
        return false;
      }
    });

    if (Bil24Application.withMiniDrawer()) {
      drawerBuilder.withHeader(R.layout.drawer_header_mini);
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);

      //не менять последовательность следующих 3 строчек
      drawer = drawerBuilder.buildView();
      miniDrawer = drawer.getMiniDrawer().withIncludeSecondaryDrawerItems(true);
      miniDrawer.withEnableSelectedMiniDrawerItemBackground(true);

      int firstWidth = (int) com.mikepenz.crossfader.util.UIUtils.convertDpToPixel(300, activity);
      int secondWidth = (int) com.mikepenz.crossfader.util.UIUtils.convertDpToPixel(72, activity);
      crossFader = new Crossfader()
          .withGmailStyleSwiping()
          .withContent(activity.findViewById(R.id.crossfade_content))
          .withFirst(drawer.getSlider(), firstWidth)
          .withSecond(miniDrawer.build(activity), secondWidth)
//        .withSavedInstance(savedInstanceState)
          .build();

      //добавим хэдер (меню)
      LayoutInflater inflater = LayoutInflater.from(activity);
      LinearLayout HEADER = (LinearLayout) inflater.inflate(R.layout.drawer_header_mini_menu, null);
      HEADER.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          crossFader.crossFade();
        }
      });
      HEADER.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      HEADER.getLayoutParams().height = UIUtils.getActionBarHeight(activity) + UIUtils.getStatusBarHeight(activity);
      HEADER.setPadding(0, UIUtils.getStatusBarHeight(activity), 0, 0);
      RecyclerView recyclerView = miniDrawer.getRecyclerView();
      recyclerView.setPadding(recyclerView.getPaddingLeft(), 0, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
      LinearLayout linearLayout = ((LinearLayout)(recyclerView.getParent()));
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      linearLayout.addView(HEADER, 0);

      miniDrawer.withCrossFader(new CrossFadeWrapper(crossFader));
      crossFader.getFirst().setBackgroundColor(activity.getResources().getColor(R.color.material_drawer_background));
      crossFader.getSecond().setBackgroundColor(activity.getResources().getColor(R.color.material_drawer_background));
      crossFader.getCrossFadeSlidingPaneLayout().setBackgroundColor(activity.getResources().getColor(R.color.material_drawer_background));
      crossFader.withPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
          drawer.getAdapter().notifyAdapterDataSetChanged();
        }

        @Override
        public void onPanelOpened(View panel) {
          Utils.hideSoftKeyboard(activity);
          Controller.getInstance().getUserInfo();
        }

        @Override
        public void onPanelClosed(View panel) {
        }
      });
      ImageView toggle = (ImageView) drawer.getHeader().findViewById(R.id.material_drawer_account_header_toggle);
      toggle.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_chevron_left));
      toggle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          crossFader.crossFade();
        }
      });
    } else {
      drawerBuilder.withHeader(R.layout.drawer_header);
      drawer = drawerBuilder.build();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //слайдер панель при свайпе заходит на статус бар
        drawer.getDrawerLayout().setFitsSystemWindows(false);
      }
    }
    Controller.getInstance().setUpdateLeftMenuListener(this);
  }

  public boolean isDrawerOpen() {
    if (miniDrawer == null) return drawer.isDrawerOpen();
    else return crossFader.isCrossFaded();
  }

  public void drawerOpen() {
    if (miniDrawer == null) drawer.openDrawer();
    else crossFader.crossFade();
  }

  public void closeDrawer() {
    if (miniDrawer == null) drawer.closeDrawer();
    else crossFader.crossFade();
  }

  public void setSelection(LeftMenu leftMenu, boolean fireOnClick) {
    drawer.setSelection(leftMenu.ordinal(), fireOnClick);
    if (miniDrawer != null) miniDrawer.setSelection(leftMenu.ordinal());
  }

  public void setSelection(LeftMenu leftMenu) {
    drawer.setSelection(leftMenu.ordinal(), true);
  }

  @Override
  public void updateLeftMenuBasketAmount(int amount) {
    changeLeftMenuAmount(amount, LeftMenu.BASKET);
  }

  @Override
  public void updateLeftMenuTicketAmount() {
    long amount = Settings.getActionEventTickets();
    changeLeftMenuAmount((int) amount, LeftMenu.MY_TICKETS);
    String topQuantity = String.valueOf((amount == 0) ? "" : amount);
    Controller.getInstance().updateTicketTopMenu(topQuantity);
  }

  @Override
  public void updateLeftMenuMECsAmount() {
    int amount = Settings.getMECSInfoSize();
    changeLeftMenuAmount(amount, LeftMenu.MY_MECS);
  }

  @Override
  public void updateLeftMenuOrderAmount(int amount) {
    changeLeftMenuAmount(amount, LeftMenu.MY_ORDERS);
  }

  @Override
  public void updateLeftMenuPromoCodesAmount(int amount) {
    changeLeftMenuAmount(amount, LeftMenu.MY_PROMO_CODES);
  }

  @Override
  public void resetLeftMenu() {
    Settings.setSeatInReserve(0);
    Settings.setOrderInWait(0);
    Settings.setMECInfoSize(0);
    Settings.setActionEventTickets(0);
    updateLeftMenuBasketAmount(0);
    updateLeftMenuMECsAmount();
    updateLeftMenuOrderAmount(0);
    updateLeftMenuPromoCodesAmount(0);
    updateLeftMenuTicketAmount();
    Controller.getInstance().updateTicketTopMenu("");
  }

  @SuppressWarnings("ConstantConditions")
  private void changeLeftMenuAmount(int amount, LeftMenu leftMenu) {
    IDrawerItem drawerItem = drawer.getDrawerItems().get(leftMenu.ordinal());
    Badgeable badgeable = (Badgeable) drawerItem;
    if (amount == 0) {
      StringHolder stringHolder = null;
      badgeable.withBadge(stringHolder);
    } else badgeable.withBadge(String.valueOf(amount));
    drawer.updateItem(drawerItem);
    if (miniDrawer != null) {
      miniDrawer.updateItem(drawerItem.getIdentifier());
    }
  }

  private class CrossFadeWrapper implements ICrossfader {
    private Crossfader mCrossFader;

    CrossFadeWrapper(Crossfader crossfader) {
      this.mCrossFader = crossfader;
    }

    @Override
    public void crossfade() {
      mCrossFader.crossFade();
    }

    @Override
    public boolean isCrossfaded() {
      return mCrossFader.isCrossFaded();
    }
  }
}