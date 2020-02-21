package com.bil24.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.activity.bank.BankWebViewClient;
import com.bil24.adapter.basket.*;
import com.bil24.dialog.*;
import com.bil24.fragments.action.filter.Kind;
import com.bil24.storage.FrontendType;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;
import server.en.OrderStatus;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;
import server.net.obj.extra.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class BasketFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BasketListener, CreateOrderListener, GetCartListener, BasketFragmentFullNameListener {
  private static final String TEXT1 = "Подготовка оплаты...";

  @BindView(R.id.basketFragmentListView)
  ListView listView;
  @BindView(R.id.basketFragmentRefresh)
  SwipeRefreshLayout refreshLayout;
  @BindView(R.id.basketFragmentTextView)
  TextView textViewEmpty;

  @BindView(R.id.basketFragmentButtonPanel)
  RelativeLayout buttonPanel;
  @BindView(R.id.basketFragmentButtonBuy)
  com.rey.material.widget.Button buttonBuy;
  @BindView(R.id.basketFragmentTextViewSum)
  TextView textViewSum;
  @BindView(R.id.basketFragmentTextViewQuantity)
  TextView textViewQuantity;
  @BindView(R.id.basketFragmentTextViewCharge)
  TextView textViewCharge;
  @BindView(R.id.basketFragmentTextViewChargeSum)
  TextView textViewChargeSum;

  @BindView(R.id.basketFragmentPromoEditText)
  EditText promoEditText;

  @BindView(R.id.basketFragmentButtonPromoCode)
  com.rey.material.widget.Button buttonPromoCode;

  private Unbinder unbinder;

  private Timer timer;
  private BigDecimal totalSum = BigDecimal.ZERO;
  private boolean openMECFragmentAfterPaid = false;
  private Map<Long, SeatForCalculating> calculatingMap = new ArrayMap<>();
  private boolean fullNameRequired = false;
  private String rmkEmail;
  private String rmkPhone;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_basket, container, false);
    unbinder = ButterKnife.bind(this, view);
    textViewEmpty.setVisibility(View.GONE);
    refreshLayout.setOnRefreshListener(this);
    buttonPanel.setVisibility(View.GONE);
    buttonBuy.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!promoEditText.getText().toString().isEmpty()) {
          Dialogs.showSnackBar(getActivity(), "");
          return;
        }
        if (Controller.getInstance().getFrontendData(getContext()).getFrontendType() == FrontendType.ANDROID) {//если не МРМК
          createOrder1();
        } else { //если МРМК
          Dialogs.showCreateOrderRmkDialog(getActivity(), getFragmentManager(), new Dialogs.CreateOrderRmkListener() {
            @Override
            public void onCreateOrderRmkSuccess(String email, String phone) {
              BasketFragment.this.rmkEmail = email;
              BasketFragment.this.rmkPhone = phone;
              createOrder1();
            }
          });
        }
      }
    });

    promoEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (promoEditText.getText().toString().isEmpty()) {
          buttonPromoCode.setVisibility(View.GONE);
          buttonBuy.setVisibility(View.VISIBLE);
        } else {
          buttonPromoCode.setVisibility(View.VISIBLE);
          buttonBuy.setVisibility(View.GONE);
        }
      }
    });

    buttonPromoCode.setVisibility(View.GONE);
    buttonBuy.setVisibility(View.VISIBLE);

    buttonPromoCode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Dialogs.addPromoCodes(new Dialogs.DialogAddPromoCodesListener() {
          @Override
          public void onSuccess(boolean newPromo) {
            promoEditText.setText("");
            if (newPromo) onRefresh();
          }
        }, getActivity(), BasketFragment.this, promoEditText.getText().toString());
      }
    });
    Controller.getInstance().setBasketFragmentFullNameListener(this);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  private void createOrder1() {
    if (fullNameRequired) {
      Dialogs.showFullNameDialog(getFragmentManager());
      return;
    }
    createOrder2();
  }

  private void createOrder2() {
    //Не показывать выбор скидки если это ОМП или сумма и так равна нулю
    if (Controller.getInstance().getFrontendData(getContext()).getFrontendType() == FrontendType.ANDROID || totalSum.compareTo(BigDecimal.ZERO) == 0) {
      createOrder3(null);
    } else {
      new DiscountDialog(new DiscountDialog.DiscountListener() {
        @Override
        public void onDiscount(final BigDecimal discount) {
          createOrder3(discount);
        }
      }, totalSum).show(getFragmentManager());
    }
  }

  private void createOrder3(BigDecimal discount) {
    Dialogs.startProcess(getActivity(), TEXT1);
    NetManager.createOrder(this, rmkEmail, rmkPhone, Settings.getLastFullName(), BankWebViewClient.successUrl, BankWebViewClient.failUrl, discount, null);
  }

  @Override
  public void onSuccessFullName(@NonNull String fullName) {
    createOrder2();
  }

  @Override
  public void onResume() {
    super.onResume();
    onRefresh();
  }

  @Override
  public void onRefresh() {
    if (!isAdded()) return;
    refreshLayout.setRefreshing(true);
    buttonPanel.setVisibility(View.GONE);
    listView.setVisibility(View.GONE);
    NetManager.getCart(this);
  }

  @Override
  public void changeSeatChecked(final List<Long> seatIdList, final boolean checked, boolean discount) {
    if (discount) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          onRefresh();
        }
      });
      return;
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        for (Long seatId : seatIdList) {
          SeatForCalculating seatForCalculating = calculatingMap.get(seatId);
          if (seatForCalculating != null) seatForCalculating.setChecked(checked);
        }
        calculateSums();
      }
    }).start();
  }

  @Override
  public void blockInterface() {
    if (!isAdded()) return;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        buttonBuy.setEnabled(false);
      }
    });
  }

  @Override
  public void unBlockInterface() {
    if (!isAdded()) return;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        buttonBuy.setEnabled(true);
      }
    });
  }

  @SuppressLint("SetTextI18n")
  private void calculateSums() {
    BigDecimal totalSum = BigDecimal.ZERO;
    BigDecimal chargeSum = BigDecimal.ZERO;
    int seatInReserve = 0;

    for (SeatForCalculating calculate : calculatingMap.values()) {
      if (!calculate.isChecked()) continue;
      totalSum = totalSum.add(calculate.getSum());
      chargeSum = chargeSum.add(calculate.getCharge());
      seatInReserve++;
    }

    if (calculatingMap.size() == 1) {
      Integer kind = calculatingMap.values().iterator().next().getKind();
      if (kind == Kind.Type.MEC) openMECFragmentAfterPaid = true;
    }

    Settings.setSeatInReserve(seatInReserve);

    final BigDecimal finalChargeSum = chargeSum;
    final BigDecimal finalTotalSum = totalSum;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {

        if (!isAdded()) return;
        if (calculatingMap.isEmpty()) cancelTimer();
        if (finalChargeSum.compareTo(BigDecimal.ZERO) > 0) {
          textViewCharge.setVisibility(View.VISIBLE);
          textViewChargeSum.setVisibility(View.VISIBLE);
        } else {
          textViewCharge.setVisibility(View.GONE);
          textViewChargeSum.setVisibility(View.GONE);
        }
        textViewChargeSum.setText(Utils.sumFormat.format(finalChargeSum) + " руб.");

        if (finalTotalSum.compareTo(BigDecimal.ZERO) == 0 && calculatingMap.isEmpty()) {
          buttonBuy.setEnabled(false);
        } else {
          buttonBuy.setEnabled(true);
        }
        BasketFragment.this.totalSum = finalTotalSum;
        textViewSum.setText(Utils.sumFormat.format(finalTotalSum) + " руб.");
        textViewQuantity.setText("Кол-во: " + Settings.getSeatInReserve() + " на сумму ");
      }
    });
  }

  public void cancelTimer() {
    if (timer != null) timer.cancel();
    changeButtonText("Оплатить заказ");
  }

  @Override
  public void onDestroy() {
    if (timer != null) timer.cancel();
    super.onDestroy();
  }

  @Override
  public void onGetCart(GetCartClient clientData) {
    Dialogs.stopProcess();
    if (!isAdded()) return;
    refreshLayout.setRefreshing(false);

    calculatingMap.clear();
    openMECFragmentAfterPaid = false;
    fullNameRequired = false;
    rmkEmail = null;
    rmkPhone = null;

    if (clientData.getActionEventList().isEmpty()) {
      textViewEmpty.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
      buttonPanel.setVisibility(View.GONE);
      Settings.setSeatInReserve(0);
    } else {
      if (timer != null) timer.cancel();
      timer = new Timer();
      timer.schedule(new MyTimerTask(clientData.getTime()), 0, 1000);

      buttonPanel.setVisibility(View.VISIBLE);
      textViewEmpty.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
      boolean discount = false;
      for (ExtraActionEventCart actionEvent : clientData.getActionEventList()) {
        if (!fullNameRequired) fullNameRequired = actionEvent.isFullNameRequired();
        for (ExtraSeatCart extraSeat : actionEvent.getSeatList()) {
          Settings.incSeatInReserve();
          calculatingMap.put(extraSeat.getSeatId(), new SeatForCalculating(extraSeat.getSeatId(), extraSeat.getPrice(), extraSeat.getServiceCharge(), actionEvent.getKindId()));
          if (extraSeat.isDiscount()) discount = true;
        }
      }
      BasketAdapter basketAdapter = new BasketAdapter(getActivity(), discount, clientData.getActionEventList(), this);
      listView.setAdapter(basketAdapter);
      calculateSums();
    }
  }

  @Override
  public void onGetCartFailed(NetException e) {
    if (!isAdded()) return;
    refreshLayout.setRefreshing(false);
  }

  class MyTimerTask extends TimerTask {
    Long time;

    MyTimerTask(Long time) {
      this.time = time;
    }

    @Override
    public void run() {
      if (time != null && time >= 0) {
        String text;
        if (time > 3600) {
          text = String.format(Locale.getDefault(), "%02d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
        }
        else {
          text = String.format(Locale.getDefault(), "%02d:%02d", (time % 3600) / 60, (time % 60));
        }
        changeButtonText("Оплатить заказ (" + text + ")");
        time--;
      } else {
        cancelTimer();
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            onRefresh();
          }
        });
      }
    }
  }

  private void changeButtonText(final String text) {
    BasketFragment.this.getActivity().runOnUiThread(() -> {
      if (!BasketFragment.this.isAdded()) return;
      buttonBuy.setText(text);
    });
  }

  @Override
  public void onCreateOrder(CreateOrderClient clientData) {
    if (!isAdded()) return;
    Settings.setSeatInReserve(0);
    Dialogs.stopProcess();

    if (Controller.getInstance().canWorkAsPrintTerminal(getContext())) {
      Dialogs.showPrintCheckDialog(getFragmentManager(), clientData.getOrderId(), clientData.getStatusExtInt());
    } else {
      if (clientData.getStatusExtInt() == OrderStatus.PAID.getId()) {
        if (openMECFragmentAfterPaid) Controller.getInstance().showMec1Fragment();
        else Controller.getInstance().showTicket1Fragment(1);
        Dialogs.showSnackBar(getActivity(), "Заказ оплачен");
      } else {
        Controller.getInstance().showAlfaActivity(clientData.getOrderId(), clientData.getFormUrl(), openMECFragmentAfterPaid);
      }
    }
  }

  @Override
  public void onCreateOrderFailed(NetException e) {
    if (!isAdded()) return;
    Dialogs.stopProcess();
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}