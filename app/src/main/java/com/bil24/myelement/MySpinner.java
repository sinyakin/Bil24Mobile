package com.bil24.myelement;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.*;
import android.database.DataSetObserver;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.os.*;
import android.support.annotation.NonNull;
import android.text.TextUtils.TruncateAt;
import android.util.*;
import android.view.*;
import android.view.ViewTreeObserver.*;
import android.view.animation.*;
import android.view.animation.Interpolator;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;
import com.rey.material.R.*;
import com.rey.material.app.ThemeManager.OnThemeChangedListener;
import com.rey.material.drawable.*;
import com.rey.material.util.ThemeUtil;
import com.rey.material.widget.FrameLayout;
import com.rey.material.widget.ListPopupWindow;
import com.rey.material.widget.ListView;
import com.rey.material.widget.TextView;

import java.util.Collection;

public class MySpinner<T> extends FrameLayout implements OnThemeChangedListener {
  private static final int MAX_ITEMS_MEASURED = 15;
  private static final int INVALID_POSITION = -1;
  private boolean mLabelEnable;
  private TextView mLabelView;
  private SpinnerAdapter mAdapter;
  private MySpinner.OnItemClickListener mOnItemClickListener;
  private MySpinner.OnItemSelectedListener mOnItemSelectedListener;
  private int mMinWidth;
  private int mMinHeight;
  private MySpinner.DropdownPopup mPopup;
  private int mDropDownWidth;
  private ArrowDrawable mArrowDrawable;
  private int mArrowSize;
  private int mArrowPadding;
  private boolean mArrowAnimSwitchMode;
  private DividerDrawable mDividerDrawable;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mGravity;
  private boolean mDisableChildrenWhenDisabled;
  private int mSelectedPosition;
  private MySpinner.RecycleBin mRecycler = new MySpinner.RecycleBin();
  private Rect mTempRect = new Rect();
  private MySpinner.DropDownAdapter mTempAdapter;
  private MySpinner.SpinnerDataSetObserver mDataSetObserver = new MySpinner.SpinnerDataSetObserver();
  private boolean mIsRtl;

  public MySpinner(Context context) {
    super(context, (AttributeSet) null, attr.listPopupWindowStyle);
  }

  public MySpinner(Context context, AttributeSet attrs) {
    super(context, attrs, attr.listPopupWindowStyle);
  }

  public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    this.mLabelEnable = false;
    this.mDropDownWidth = -2;
    this.mArrowAnimSwitchMode = false;
    this.mGravity = 17;
    this.mDisableChildrenWhenDisabled = false;
    this.mSelectedPosition = -1;
    this.mIsRtl = false;
    this.setWillNotDraw(false);
    this.mPopup = new MySpinner.DropdownPopup(context, attrs, defStyleAttr, defStyleRes);
    this.mPopup.setModal(true);
    if (this.isInEditMode()) {
      this.applyStyle(style.Material_Widget_Spinner);
    }

    this.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        MySpinner.this.showPopup();
      }
    });
    super.init(context, attrs, defStyleAttr, defStyleRes);
  }

  public void setData(Collection<T> list) {
    setData(list, null, null);
  }

  public void setData(Collection<T> list, OnItemSelectedListener listener) {
    setData(list, null, listener);
  }

  public void setData(Collection<T> list, T t) {
    setData(list, t, null);
  }

  @SuppressWarnings("unchecked")
  public void setData(Collection<T> list, T t, OnItemSelectedListener listener) {
    if (mAdapter == null) return;
    try {
      setOnItemSelectedListener(null);
      ArrayAdapter<T> arrayAdapter = (ArrayAdapter<T>) mAdapter;
      arrayAdapter.clear();
      arrayAdapter.addAll(list);
      setOnItemSelectedListener(listener);

      if (t != null) {
        int position = arrayAdapter.getPosition(t);
        if (position != -1) {
          if (position == getSelectedItemPosition()) mSelectedPosition = -1;
          setSelection(position);
        } else if (!list.isEmpty()) setSelection(0);
      } else if (!list.isEmpty()) setSelection(0);

    } catch (Exception ignored) {}
  }

  private android.widget.TextView getLabelView() {
    if (this.mLabelView == null) {
      this.mLabelView = new TextView(this.getContext());
      if (VERSION.SDK_INT >= 17) {
        this.mLabelView.setTextDirection(this.mIsRtl ? 4 : 3);
      }

      this.mLabelView.setSingleLine(true);
      this.mLabelView.setDuplicateParentStateEnabled(true);
    }

    return this.mLabelView;
  }

  protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super.applyStyle(context, attrs, defStyleAttr, defStyleRes);
    this.removeAllViews();
    TypedArray a = context.obtainStyledAttributes(attrs, styleable.Spinner, defStyleAttr, defStyleRes);
    int arrowAnimDuration = -1;
    ColorStateList arrowColor = null;
    Interpolator arrowInterpolator = null;
    boolean arrowClockwise = true;
    int dividerAnimDuration = -1;
    ColorStateList dividerColor = null;
    ColorStateList labelTextColor = null;
    int labelTextSize = -1;
    int tv = 0;

    for (int colors = a.getIndexCount(); tv < colors; ++tv) {
      int attr = a.getIndex(tv);
      if (attr == styleable.Spinner_spn_labelEnable) {
        this.mLabelEnable = a.getBoolean(attr, false);
      } else if (attr == styleable.Spinner_spn_labelPadding) {
        this.getLabelView().setPadding(0, 0, 0, a.getDimensionPixelSize(attr, 0));
      } else if (attr == styleable.Spinner_spn_labelTextSize) {
        labelTextSize = a.getDimensionPixelSize(attr, 0);
      } else if (attr == styleable.Spinner_spn_labelTextColor) {
        labelTextColor = a.getColorStateList(attr);
      } else if (attr == styleable.Spinner_spn_labelTextAppearance) {
        this.getLabelView().setTextAppearance(context, a.getResourceId(attr, 0));
      } else {
        int resId;
        if (attr == styleable.Spinner_spn_labelEllipsize) {
          resId = a.getInteger(attr, 0);
          switch (resId) {
            case 1:
              this.getLabelView().setEllipsize(TruncateAt.START);
              break;
            case 2:
              this.getLabelView().setEllipsize(TruncateAt.MIDDLE);
              break;
            case 3:
              this.getLabelView().setEllipsize(TruncateAt.END);
              break;
            case 4:
              this.getLabelView().setEllipsize(TruncateAt.MARQUEE);
              break;
            default:
              this.getLabelView().setEllipsize(TruncateAt.END);
          }
        } else if (attr == styleable.Spinner_spn_label) {
          this.getLabelView().setText(a.getString(attr));
        } else if (attr == styleable.Spinner_android_gravity) {
          this.mGravity = a.getInt(attr, 0);
        } else if (attr == styleable.Spinner_android_minWidth) {
          this.setMinimumWidth(a.getDimensionPixelOffset(attr, 0));
        } else if (attr == styleable.Spinner_android_minHeight) {
          this.setMinimumHeight(a.getDimensionPixelOffset(attr, 0));
        } else if (attr == styleable.Spinner_android_dropDownWidth) {
          this.mDropDownWidth = a.getLayoutDimension(attr, -2);
        } else if (attr == styleable.Spinner_android_popupBackground) {
          this.mPopup.setBackgroundDrawable(a.getDrawable(attr));
        } else if (attr == styleable.Spinner_android_prompt) {
          this.mPopup.setPromptText(a.getString(attr));
        } else if (attr == styleable.Spinner_spn_popupItemAnimation) {
          this.mPopup.setItemAnimation(a.getResourceId(attr, 0));
        } else if (attr == styleable.Spinner_spn_popupItemAnimOffset) {
          this.mPopup.setItemAnimationOffset(a.getInteger(attr, 0));
        } else if (attr == styleable.Spinner_spn_disableChildrenWhenDisabled) {
          this.mDisableChildrenWhenDisabled = a.getBoolean(attr, false);
        } else if (attr == styleable.Spinner_spn_arrowSwitchMode) {
          this.mArrowAnimSwitchMode = a.getBoolean(attr, false);
        } else if (attr == styleable.Spinner_spn_arrowAnimDuration) {
          arrowAnimDuration = a.getInteger(attr, 0);
        } else if (attr == styleable.Spinner_spn_arrowSize) {
          this.mArrowSize = a.getDimensionPixelSize(attr, 0);
        } else if (attr == styleable.Spinner_spn_arrowPadding) {
          this.mArrowPadding = a.getDimensionPixelSize(attr, 0);
        } else if (attr == styleable.Spinner_spn_arrowColor) {
          arrowColor = a.getColorStateList(attr);
        } else if (attr == styleable.Spinner_spn_arrowInterpolator) {
          resId = a.getResourceId(attr, 0);
          arrowInterpolator = AnimationUtils.loadInterpolator(context, resId);
        } else if (attr == styleable.Spinner_spn_arrowAnimClockwise) {
          arrowClockwise = a.getBoolean(attr, true);
        } else if (attr == styleable.Spinner_spn_dividerHeight) {
          this.mDividerHeight = a.getDimensionPixelOffset(attr, 0);
        } else if (attr == styleable.Spinner_spn_dividerPadding) {
          this.mDividerPadding = a.getDimensionPixelOffset(attr, 0);
        } else if (attr == styleable.Spinner_spn_dividerAnimDuration) {
          dividerAnimDuration = a.getInteger(attr, 0);
        } else if (attr == styleable.Spinner_spn_dividerColor) {
          dividerColor = a.getColorStateList(attr);
        }
      }
    }

    a.recycle();
    if (labelTextColor != null) {
      this.getLabelView().setTextColor(labelTextColor);
    }

    if (labelTextSize >= 0) {
      this.getLabelView().setTextSize(0, (float) labelTextSize);
    }

    if (this.mLabelEnable) {
      this.addView(this.getLabelView(), 0, new LayoutParams(-2, -2));
    }

    if (this.mArrowSize > 0) {
      if (this.mArrowDrawable == null) {
        if (arrowColor == null) {
          arrowColor = ColorStateList.valueOf(ThemeUtil.colorControlNormal(context, -16777216));
        }

        if (arrowAnimDuration < 0) {
          arrowAnimDuration = 0;
        }

        this.mArrowDrawable = new ArrowDrawable(ArrowDrawable.MODE_DOWN, this.mArrowSize, arrowColor, arrowAnimDuration, arrowInterpolator, arrowClockwise);
        this.mArrowDrawable.setCallback(this);
      } else {
        this.mArrowDrawable.setArrowSize(this.mArrowSize);
        this.mArrowDrawable.setClockwise(arrowClockwise);
        if (arrowColor != null) {
          this.mArrowDrawable.setColor(arrowColor);
        }

        if (arrowAnimDuration >= 0) {
          this.mArrowDrawable.setAnimationDuration(arrowAnimDuration);
        }

        if (arrowInterpolator != null) {
          this.mArrowDrawable.setInterpolator(arrowInterpolator);
        }
      }
    } else if (this.mArrowDrawable != null) {
      this.mArrowDrawable.setCallback((Callback) null);
      this.mArrowDrawable = null;
    }

    if (this.mDividerHeight > 0) {
      if (this.mDividerDrawable == null) {
        if (dividerAnimDuration < 0) {
          dividerAnimDuration = 0;
        }

        if (dividerColor == null) {
          int[][] var18 = new int[][]{{-16842919}, {16842919, 16842910}};
          int[] var20 = new int[]{ThemeUtil.colorControlNormal(context, -16777216), ThemeUtil.colorControlActivated(context, -16777216)};
          dividerColor = new ColorStateList(var18, var20);
        }

        this.mDividerDrawable = new DividerDrawable(this.mDividerHeight, dividerColor, dividerAnimDuration);
        this.mDividerDrawable.setCallback(this);
      } else {
        this.mDividerDrawable.setDividerHeight(this.mDividerHeight);
        if (dividerColor != null) {
          this.mDividerDrawable.setColor(dividerColor);
        }

        if (dividerAnimDuration >= 0) {
          this.mDividerDrawable.setAnimationDuration(dividerAnimDuration);
        }
      }
    } else if (this.mDividerDrawable != null) {
      this.mDividerDrawable.setCallback((Callback) null);
      this.mDividerDrawable = null;
    }

    if (this.mTempAdapter != null) {
      this.mPopup.setAdapter(this.mTempAdapter);
      this.mTempAdapter = null;
    }

    if (this.mAdapter != null) {
      this.setAdapter(this.mAdapter);
    }

    if (this.isInEditMode()) {
      TextView var19 = new TextView(context, attrs, defStyleAttr);
      var19.setText("Item 1");
      super.addView(var19);
    }

    this.requestLayout();
  }

  @TargetApi(17)
  public void onRtlPropertiesChanged(int layoutDirection) {
    boolean rtl = layoutDirection == View.LAYOUT_DIRECTION_RTL;
    if (this.mIsRtl != rtl) {
      this.mIsRtl = rtl;
      if (this.mLabelView != null && VERSION.SDK_INT >= 17) {
        this.mLabelView.setTextDirection(this.mIsRtl ? 4 : 3);
      }

      this.requestLayout();
    }

  }

  public View getSelectedView() {
    View v = this.getChildAt(this.getChildCount() - 1);
    return v == this.mLabelView ? null : v;
  }

  public void setSelection(int position) {
    if (this.mAdapter != null) {
      position = Math.max(0, Math.min(position, this.mAdapter.getCount() - 1));
    }

    if (this.mSelectedPosition != position) {
      this.mSelectedPosition = position;
      if (this.mOnItemSelectedListener != null) {
        this.mOnItemSelectedListener.onItemSelected(this, this.getSelectedView(), position, this.mAdapter == null ? -1L : this.mAdapter.getItemId(position));
      }

      this.onDataInvalidated();
    }
  }

  public int getSelectedItemPosition() {
    return this.mSelectedPosition;
  }

  @SuppressWarnings("unchecked")
  public T getSelectedItem() {
    return this.mAdapter == null ? null : (T) this.mAdapter.getItem(this.mSelectedPosition);
  }

  public SpinnerAdapter getAdapter() {
    return this.mAdapter;
  }

  public void setAdapter(SpinnerAdapter adapter) {
    if (this.mAdapter != null) {
      this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
    }

    this.mRecycler.clear();
    this.mAdapter = adapter;
    this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
    this.onDataChanged();
    if (this.mPopup != null) {
      this.mPopup.setAdapter(new MySpinner.DropDownAdapter(adapter));
    } else {
      this.mTempAdapter = new MySpinner.DropDownAdapter(adapter);
    }

  }

  public void setPopupBackgroundDrawable(Drawable background) {
    this.mPopup.setBackgroundDrawable(background);
  }

  public void setPopupBackgroundResource(int resId) {
    this.setPopupBackgroundDrawable(this.getContext().getDrawable(resId));
  }

  public Drawable getPopupBackground() {
    return this.mPopup.getBackground();
  }

  public void setDropDownVerticalOffset(int pixels) {
    this.mPopup.setVerticalOffset(pixels);
  }

  public int getDropDownVerticalOffset() {
    return this.mPopup.getVerticalOffset();
  }

  public void setDropDownHorizontalOffset(int pixels) {
    this.mPopup.setHorizontalOffset(pixels);
  }

  public int getDropDownHorizontalOffset() {
    return this.mPopup.getHorizontalOffset();
  }

  public void setDropDownWidth(int pixels) {
    this.mDropDownWidth = pixels;
  }

  public int getDropDownWidth() {
    return this.mDropDownWidth;
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (this.mDisableChildrenWhenDisabled) {
      int count = this.getChildCount();

      for (int i = 0; i < count; ++i) {
        this.getChildAt(i).setEnabled(enabled);
      }
    }

  }

  public void setMinimumHeight(int minHeight) {
    this.mMinHeight = minHeight;
    super.setMinimumHeight(minHeight);
  }

  public void setMinimumWidth(int minWidth) {
    this.mMinWidth = minWidth;
    super.setMinimumWidth(minWidth);
  }

  public void setGravity(int gravity) {
    if (this.mGravity != gravity) {
      if ((gravity & 7) == 0) {
        gravity |= 8388611;
      }

      this.mGravity = gravity;
      this.requestLayout();
    }

  }

  public int getBaseline() {
    View child = this.getSelectedView();
    if (child != null) {
      int childBaseline = child.getBaseline();
      if (childBaseline < 0) {
        return -1;
      } else {
        int paddingTop = this.getPaddingTop();
        if (this.mLabelView != null) {
          paddingTop += this.mLabelView.getMeasuredHeight();
        }

        int remainHeight = this.getMeasuredHeight() - paddingTop - this.getPaddingBottom() - this.getDividerDrawableHeight();
        int verticalGravity = this.mGravity & 112;
        switch (verticalGravity) {
          case 48:
            return paddingTop + childBaseline;
          case 80:
            return paddingTop + remainHeight - child.getMeasuredHeight() + childBaseline;
          default:
            return (remainHeight - child.getMeasuredHeight()) / 2 + paddingTop + childBaseline;
        }
      }
    } else {
      return -1;
    }
  }

  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.mPopup != null && this.mPopup.isShowing()) {
      this.mPopup.dismiss();
    }

  }

  public void setOnItemClickListener(MySpinner.OnItemClickListener l) {
    this.mOnItemClickListener = l;
  }

  public void setOnItemSelectedListener(MySpinner.OnItemSelectedListener l) {
    this.mOnItemSelectedListener = l;
  }

  public boolean onInterceptTouchEvent(MotionEvent event) {
    return true;
  }

  protected boolean verifyDrawable(Drawable who) {
    return super.verifyDrawable(who) || this.mArrowDrawable == who || this.mDividerDrawable == who;
  }

  private int getArrowDrawableWidth() {
    return this.mArrowDrawable != null ? this.mArrowSize + this.mArrowPadding * 2 : 0;
  }

  private int getDividerDrawableHeight() {
    return this.mDividerHeight > 0 ? this.mDividerHeight + this.mDividerPadding : 0;
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int paddingHorizontal = this.getPaddingLeft() + this.getPaddingRight() + this.getArrowDrawableWidth();
    int paddingVertical = this.getPaddingTop() + this.getPaddingBottom() + this.getDividerDrawableHeight();
    int labelWidth = 0;
    int labelHeight = 0;
    if (this.mLabelView != null && this.mLabelView.getLayoutParams() != null) {
      this.mLabelView.measure(MeasureSpec.makeMeasureSpec(widthSize - paddingHorizontal, widthMode), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      labelWidth = this.mLabelView.getMeasuredWidth();
      labelHeight = this.mLabelView.getMeasuredHeight();
    }

    int width = 0;
    int height = 0;
    View v = this.getSelectedView();
    int viewWidth;
    if (v != null) {
      ViewGroup.LayoutParams viewHeight = v.getLayoutParams();
      int params;
      switch (viewHeight.width) {
        case -2:
          params = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          break;
        case -1:
          params = MeasureSpec.makeMeasureSpec(widthSize - paddingHorizontal, widthMode);
          break;
        default:
          params = MeasureSpec.makeMeasureSpec(viewHeight.width, MeasureSpec.EXACTLY);
      }

      switch (viewHeight.height) {
        case -2:
          viewWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
          break;
        case -1:
          viewWidth = MeasureSpec.makeMeasureSpec(heightSize - paddingVertical - labelHeight, heightMode);
          break;
        default:
          viewWidth = MeasureSpec.makeMeasureSpec(viewHeight.height, MeasureSpec.EXACTLY);
      }

      v.measure(params, viewWidth);
      width = v.getMeasuredWidth();
      height = v.getMeasuredHeight();
    }

    width = Math.max(this.mMinWidth, Math.max(labelWidth, width) + paddingHorizontal);
    height = Math.max(this.mMinHeight, height + labelHeight + paddingVertical);
    switch (widthMode) {
      case -2147483648:
        width = Math.min(widthSize, width);
        break;
      case 1073741824:
        width = widthSize;
    }

    switch (heightMode) {
      case -2147483648:
        height = Math.min(heightSize, height);
        break;
      case 1073741824:
        height = heightSize;
    }

    this.setMeasuredDimension(width, height);
    if (v != null) {
      ViewGroup.LayoutParams params1 = v.getLayoutParams();
      switch (params1.width) {
        case -2:
          viewWidth = v.getMeasuredWidth();
          break;
        case -1:
          viewWidth = width - paddingHorizontal;
          break;
        default:
          viewWidth = params1.width;
      }

      int viewHeight1;
      switch (params1.height) {
        case -2:
          viewHeight1 = v.getMeasuredHeight();
          break;
        case -1:
          viewHeight1 = height - labelHeight - paddingVertical;
          break;
        default:
          viewHeight1 = params1.height;
      }

      if (v.getMeasuredWidth() != viewWidth || v.getMeasuredHeight() != viewHeight1) {
        v.measure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight1, MeasureSpec.EXACTLY));
      }
    }

  }

  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int w = r - l;
    int h = b - t;
    int arrowWidth = this.getArrowDrawableWidth();
    int childLeft;
    int childRight;
    if (this.mArrowDrawable != null) {
      childLeft = this.getPaddingTop() + (this.mLabelView == null ? 0 : this.mLabelView.getMeasuredHeight());
      childRight = h - this.getDividerDrawableHeight() - this.getPaddingBottom();
      if (this.mIsRtl) {
        this.mArrowDrawable.setBounds(this.getPaddingLeft(), childLeft, this.getPaddingLeft() + arrowWidth, childRight);
      } else {
        this.mArrowDrawable.setBounds(this.getWidth() - this.getPaddingRight() - arrowWidth, childLeft, this.getWidth() - this.getPaddingRight(), childRight);
      }
    }

    if (this.mDividerDrawable != null) {
      this.mDividerDrawable.setBounds(this.getPaddingLeft(), h - this.mDividerHeight - this.getPaddingBottom(), w - this.getPaddingRight(), h - this.getPaddingBottom());
    }

    childLeft = this.mIsRtl ? this.getPaddingLeft() + arrowWidth : this.getPaddingLeft();
    childRight = this.mIsRtl ? w - this.getPaddingRight() : w - this.getPaddingRight() - arrowWidth;
    int childTop = this.getPaddingTop();
    int childBottom = h - this.getPaddingBottom();
    if (this.mLabelView != null) {
      if (this.mIsRtl) {
        this.mLabelView.layout(childRight - this.mLabelView.getMeasuredWidth(), childTop, childRight, childTop + this.mLabelView.getMeasuredHeight());
      } else {
        this.mLabelView.layout(childLeft, childTop, childLeft + this.mLabelView.getMeasuredWidth(), childTop + this.mLabelView.getMeasuredHeight());
      }

      childTop += this.mLabelView.getMeasuredHeight();
    }

    View v = this.getSelectedView();
    if (v != null) {
      int horizontalGravity = this.mGravity & 7;
      if (horizontalGravity == 8388611) {
        horizontalGravity = this.mIsRtl ? 5 : 3;
      } else if (horizontalGravity == 8388613) {
        horizontalGravity = this.mIsRtl ? 3 : 5;
      }

      int x;
      switch (horizontalGravity) {
        case 1:
          x = (childRight - childLeft - v.getMeasuredWidth()) / 2 + childLeft;
          break;
        case 2:
        case 4:
        default:
          x = (childRight - childLeft - v.getMeasuredWidth()) / 2 + childLeft;
          break;
        case 3:
          x = childLeft;
          break;
        case 5:
          x = childRight - v.getMeasuredWidth();
      }

      int verticalGravity = this.mGravity & 112;
      int y;
      switch (verticalGravity) {
        case 16:
          y = (childBottom - childTop - v.getMeasuredHeight()) / 2 + childTop;
          break;
        case 48:
          y = childTop;
          break;
        case 80:
          y = childBottom - v.getMeasuredHeight();
          break;
        default:
          y = (childBottom - childTop - v.getMeasuredHeight()) / 2 + childTop;
      }

      v.layout(x, y, x + v.getMeasuredWidth(), y + v.getMeasuredHeight());
    }

  }

  public void draw(@NonNull Canvas canvas) {
    super.draw(canvas);
    if (this.mDividerDrawable != null) {
      this.mDividerDrawable.draw(canvas);
    }

    if (this.mArrowDrawable != null) {
      this.mArrowDrawable.draw(canvas);
    }

  }

  protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (this.mArrowDrawable != null) {
      this.mArrowDrawable.setState(this.getDrawableState());
    }

    if (this.mDividerDrawable != null) {
      this.mDividerDrawable.setState(this.getDrawableState());
    }

  }

  public boolean performItemClick(View view, int position, long id) {
    if (this.mOnItemClickListener != null) {
      if (this.mOnItemClickListener.onItemClick(this, view, position, id)) {
        this.setSelection(position);
      }

      return true;
    } else {
      this.setSelection(position);
      return false;
    }
  }

  private void onDataChanged() {
    if (this.mSelectedPosition == -1) {
      this.setSelection(0);
    } else if (this.mSelectedPosition < this.mAdapter.getCount()) {
      this.onDataInvalidated();
    } else if (this.mSelectedPosition == this.mAdapter.getCount() && this.mAdapter.getCount() == 0) {
      this.removeAllViews();
    } else {
      this.setSelection(this.mAdapter.getCount() - 1);
    }

  }

  private void onDataInvalidated() {
    if (this.mAdapter != null) {
      int type;
      if (this.mLabelView == null) {
        this.removeAllViews();
      } else {
        for (type = this.getChildCount() - 1; type > 0; --type) {
          this.removeViewAt(type);
        }
      }

      if (mAdapter.getCount() != 0) {
        type = this.mAdapter.getItemViewType(this.mSelectedPosition);
        View v = this.mAdapter.getView(this.mSelectedPosition, this.mRecycler.get(type), this);
        v.setFocusable(false);
        v.setClickable(false);
        if (v.getParent() != null) {
          ((ViewGroup) v.getParent()).removeView(v);
        }

        super.addView(v);
        this.mRecycler.put(type, v);
      }
    }
  }

  private void showPopup() {
    if (!this.mPopup.isShowing()) {
      this.mPopup.show();
      final ListView lv = this.mPopup.getListView();
      if (lv != null) {
        if (VERSION.SDK_INT >= 11) {
          lv.setChoiceMode(1);
        }

        lv.setSelection(this.getSelectedItemPosition());
        if (this.mArrowDrawable != null && this.mArrowAnimSwitchMode) {
          lv.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
              lv.getViewTreeObserver().removeOnPreDrawListener(this);
              MySpinner.this.mArrowDrawable.setMode(ArrowDrawable.MODE_UP, true);
              return true;
            }
          });
        }
      }
    }

  }

  private void onPopupDismissed() {
    if (this.mArrowDrawable != null) {
      this.mArrowDrawable.setMode(ArrowDrawable.MODE_DOWN, true);
    }

  }

  private int measureContentWidth(SpinnerAdapter adapter, Drawable background) {
    if (adapter == null) {
      return 0;
    } else {
      int width = 0;
      View itemView = null;
      int itemType = 0;
      int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
      int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
      int start = Math.max(0, this.getSelectedItemPosition());
      int end = Math.min(adapter.getCount(), start + 15);
      int count = end - start;
      start = Math.max(0, start - (15 - count));

      for (int i = start; i < end; ++i) {
        int positionType = adapter.getItemViewType(i);
        if (positionType != itemType) {
          itemType = positionType;
          itemView = null;
        }

        itemView = adapter.getView(i, itemView, (ViewGroup) null);
        if (itemView.getLayoutParams() == null) {
          itemView.setLayoutParams(new LayoutParams(-2, -2));
        }

        itemView.measure(widthMeasureSpec, heightMeasureSpec);
        width = Math.max(width, itemView.getMeasuredWidth());
      }

      if (background != null) {
        background.getPadding(this.mTempRect);
        width += this.mTempRect.left + this.mTempRect.right;
      }

      return width;
    }
  }

  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    MySpinner.SavedState ss = new MySpinner.SavedState(superState);
    ss.position = this.getSelectedItemPosition();
    ss.showDropdown = this.mPopup != null && this.mPopup.isShowing();
    return ss;
  }

  public void onRestoreInstanceState(Parcelable state) {
    MySpinner.SavedState ss = (MySpinner.SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    this.setSelection(ss.position);
    if (ss.showDropdown) {
      ViewTreeObserver vto = this.getViewTreeObserver();
      if (vto != null) {
        OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
          public void onGlobalLayout() {
            MySpinner.this.showPopup();
            ViewTreeObserver vto = MySpinner.this.getViewTreeObserver();
            if (vto != null) {
              vto.removeGlobalOnLayoutListener(this);
            }

          }
        };
        vto.addOnGlobalLayoutListener(listener);
      }
    }

  }

  private class DropdownPopup extends ListPopupWindow {
    private CharSequence mHintText;
    private MySpinner.DropDownAdapter mAdapter;
    private OnGlobalLayoutListener layoutListener = new OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        DropdownPopup.this.computeContentWidth();
        MySpinner.DropdownPopup.super.show();
      }
    };

    public DropdownPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      this.setAnchorView(MySpinner.this);
      this.setModal(true);
      this.setPromptPosition(0);
      this.setOnDismissListener(new OnDismissListener() {
        @TargetApi(16)
        public void onDismiss() {
          ViewTreeObserver vto = MySpinner.this.getViewTreeObserver();
          if (vto != null) {
            if (VERSION.SDK_INT >= 16) {
              vto.removeOnGlobalLayoutListener(DropdownPopup.this.layoutListener);
            } else {
              vto.removeGlobalOnLayoutListener(DropdownPopup.this.layoutListener);
            }
          }

          MySpinner.this.onPopupDismissed();
        }
      });
    }

    public void setAdapter(ListAdapter adapter) {
      super.setAdapter(adapter);
      this.mAdapter = (MySpinner.DropDownAdapter) adapter;
      this.mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
          MySpinner.this.performItemClick(v, position, DropdownPopup.this.mAdapter.getItemId(position));
          DropdownPopup.this.dismiss();
        }
      });
    }

    public CharSequence getHintText() {
      return this.mHintText;
    }

    public void setPromptText(CharSequence hintText) {
      this.mHintText = hintText;
    }

    void computeContentWidth() {
      Drawable background = this.getBackground();
      int hOffset = 0;
      if (background != null) {
        background.getPadding(MySpinner.this.mTempRect);
        hOffset = MySpinner.this.mIsRtl ? MySpinner.this.mTempRect.right : -MySpinner.this.mTempRect.left;
      } else {
        MySpinner.this.mTempRect.left = MySpinner.this.mTempRect.right = 0;
      }

      int spinnerPaddingLeft = MySpinner.this.getPaddingLeft();
      int spinnerPaddingRight = MySpinner.this.getPaddingRight();
      int spinnerWidth = MySpinner.this.getWidth();
      if (MySpinner.this.mDropDownWidth == -2) {
        int contentWidth = MySpinner.this.measureContentWidth(this.mAdapter, this.getBackground());
        int contentWidthLimit = MySpinner.this.getContext().getResources().getDisplayMetrics().widthPixels - MySpinner.this.mTempRect.left - MySpinner.this.mTempRect.right;
        if (contentWidth > contentWidthLimit) {
          contentWidth = contentWidthLimit;
        }

        this.setContentWidth(Math.max(contentWidth, spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight));
      } else if (MySpinner.this.mDropDownWidth == -1) {
        this.setContentWidth(spinnerWidth - spinnerPaddingLeft - spinnerPaddingRight);
      } else {
        this.setContentWidth(MySpinner.this.mDropDownWidth);
      }

      if (MySpinner.this.mIsRtl) {
        hOffset += spinnerWidth - spinnerPaddingRight - this.getWidth();
      } else {
        hOffset += spinnerPaddingLeft;
      }

      this.setHorizontalOffset(hOffset);
    }

    public void show() {
      boolean wasShowing = this.isShowing();
      this.computeContentWidth();
      this.setInputMethodMode(2);
      super.show();
      if (!wasShowing) {
        ViewTreeObserver vto = MySpinner.this.getViewTreeObserver();
        if (vto != null) {
          vto.addOnGlobalLayoutListener(this.layoutListener);
        }

      }
    }
  }

  private static class DropDownAdapter implements ListAdapter, SpinnerAdapter, OnClickListener {
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public DropDownAdapter(SpinnerAdapter adapter) {
      this.mAdapter = adapter;
      if (adapter instanceof ListAdapter) {
        this.mListAdapter = (ListAdapter) adapter;
      }

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
      this.mOnItemClickListener = listener;
    }

    public void onClick(View v) {
      int position = ((Integer) v.getTag()).intValue();
      if (this.mOnItemClickListener != null) {
        this.mOnItemClickListener.onItemClick((AdapterView) null, v, position, 0L);
      }

    }

    public int getCount() {
      return this.mAdapter == null ? 0 : this.mAdapter.getCount();
    }

    public Object getItem(int position) {
      return this.mAdapter == null ? null : this.mAdapter.getItem(position);
    }

    public long getItemId(int position) {
      return this.mAdapter == null ? -1L : this.mAdapter.getItemId(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      View v = this.getDropDownView(position, convertView, parent);
      v.setOnClickListener(this);
      v.setTag(Integer.valueOf(position));
      return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
      return this.mAdapter == null ? null : this.mAdapter.getDropDownView(position, convertView, parent);
    }

    public boolean hasStableIds() {
      return this.mAdapter != null && this.mAdapter.hasStableIds();
    }

    public boolean areAllItemsEnabled() {
      ListAdapter adapter = this.mListAdapter;
      return adapter == null || adapter.areAllItemsEnabled();
    }

    public boolean isEnabled(int position) {
      ListAdapter adapter = this.mListAdapter;
      return adapter == null || adapter.isEnabled(position);
    }

    public int getItemViewType(int position) {
      ListAdapter adapter = this.mListAdapter;
      return adapter != null ? adapter.getItemViewType(position) : 0;
    }

    public int getViewTypeCount() {
      ListAdapter adapter = this.mListAdapter;
      return adapter != null ? adapter.getViewTypeCount() : 1;
    }

    public boolean isEmpty() {
      return this.getCount() == 0;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
      if (this.mAdapter != null) {
        this.mAdapter.registerDataSetObserver(observer);
      }

    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (this.mAdapter != null) {
        this.mAdapter.unregisterDataSetObserver(observer);
      }

    }
  }

  private class RecycleBin {
    private final SparseArray<View> mScrapHeap = new SparseArray<>();

    public void put(int position, View v) {
      mScrapHeap.put(position, v);
    }

    View get(int position) {
      View result = mScrapHeap.get(position);
      if (result != null) {
        mScrapHeap.delete(position);
      }

      return result;
    }

    void clear() {
      final SparseArray<View> scrapHeap = mScrapHeap;
      final int count = scrapHeap.size();
      for (int i = 0; i < count; i++) {
        final View view = scrapHeap.valueAt(i);
        if (view != null) {
          removeDetachedView(view, true);
        }
      }
      scrapHeap.clear();
    }
  }

  private class SpinnerDataSetObserver extends DataSetObserver {
    private SpinnerDataSetObserver() {
    }

    public void onChanged() {
      MySpinner.this.onDataChanged();
    }

    public void onInvalidated() {
      MySpinner.this.onDataInvalidated();
    }
  }

  static class SavedState extends BaseSavedState {
    int position;
    boolean showDropdown;
    public static final Creator<SavedState> CREATOR = new Creator() {
      public MySpinner.SavedState createFromParcel(Parcel in) {
        return new MySpinner.SavedState(in);
      }

      public MySpinner.SavedState[] newArray(int size) {
        return new MySpinner.SavedState[size];
      }
    };

    SavedState(Parcelable superState) {
      super(superState);
    }

    SavedState(Parcel in) {
      super(in);
      this.position = in.readInt();
      this.showDropdown = in.readByte() != 0;
    }

    public void writeToParcel(@NonNull Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(this.position);
      out.writeByte((byte) (this.showDropdown ? 1 : 0));
    }

    public String toString() {
      return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + " showDropdown=" + this.showDropdown + "}";
    }
  }

  public interface OnItemSelectedListener {
    void onItemSelected(MySpinner spinner, View view, int position, long l);
  }

  public interface OnItemClickListener {
    boolean onItemClick(MySpinner spinner, View view, int position, long l);
  }
}