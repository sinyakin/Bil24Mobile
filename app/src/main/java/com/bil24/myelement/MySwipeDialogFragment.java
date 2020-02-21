package com.bil24.myelement;

import android.animation.*;
import android.graphics.*;
import android.view.*;

/**
 * Created by SVV on 23.12.2015
 */
public abstract class MySwipeDialogFragment extends MyDialogFragment implements View.OnTouchListener {

  private int previousFingerPosition = 0;
  private int baseLayoutPosition = 0;
  private int defaultViewHeight;

  private boolean isClosing = false;
  private boolean isScrollingUp = false;
  private boolean isScrollingDown = false;
  protected View mainPanel;
  private int currentYPosition;
  private long swipeTime;

  @Override
  public void onResume() {
    super.onResume();
    setSwipeMainPanel();
    if (mainPanel != null) {
      mainPanel.setOnTouchListener(this);
      ViewParent viewParent = mainPanel.getParent();
      if (viewParent instanceof View) {
        ((View) viewParent).setBackgroundColor(Color.TRANSPARENT);
      }
    }
  }

  protected abstract void setSwipeMainPanel();

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (mainPanel == null) return false;

    // Get finger position on screen
    final int Y = (int) event.getRawY();

    // Switch on motion event type
    switch (event.getAction() & MotionEvent.ACTION_MASK) {

      case MotionEvent.ACTION_DOWN:
        swipeTime = System.currentTimeMillis();
        // save default base layout height
        defaultViewHeight = mainPanel.getHeight();

        // Init finger and view position
        previousFingerPosition = Y;
        baseLayoutPosition = (int) mainPanel.getY();
        break;

      case MotionEvent.ACTION_UP:
        if (System.currentTimeMillis() - swipeTime < 200 && currentYPosition > 80) {
          closeDownAndDismissDialog(currentYPosition);
          return true;
        }
        // If user was doing a scroll up
        if (isScrollingUp) {
          // Reset baselayout position
          mainPanel.setY(0);
          // We are not in scrolling up mode anymore
          isScrollingUp = false;
        }

        // If user was doing a scroll down
        if (isScrollingDown) {
          if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
            break;
          }
          // Reset base layout size
          mainPanel.getLayoutParams().height = defaultViewHeight;
          mainPanel.requestLayout();
          // We are not in scrolling down mode anymore
          isScrollingDown = false;
          ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mainPanel, "y", currentYPosition, 0).setDuration(300);
          objectAnimator.start();
          currentYPosition = 0;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (!isClosing) {
          currentYPosition = (int) mainPanel.getY();

          // If we scroll up
          if (previousFingerPosition > Y) {
            // First time android rise an event for "up" move
            if (!isScrollingUp) {
              isScrollingUp = true;
            }

            // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
            if (mainPanel.getHeight() < defaultViewHeight) {
              mainPanel.getLayoutParams().height = mainPanel.getHeight() - (Y - previousFingerPosition);
              mainPanel.requestLayout();
            } else {
              // Has user scroll enough to "auto close" popup ?
              if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                closeUpAndDismissDialog(currentYPosition);
                return true;
              }

              //
            }
            mainPanel.setY(mainPanel.getY() + (Y - previousFingerPosition));

          }
          // If we scroll down
          else {
            // First time android rise an event for "down" move
            if (!isScrollingDown) {
              isScrollingDown = true;
            }

            // Has user scroll enough to "auto close" popup ?
            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
              closeDownAndDismissDialog(currentYPosition);
              return true;
            }

            // Change base layout size and position (must change position because view anchor is top left corner)
            mainPanel.setY(mainPanel.getY() + (Y - previousFingerPosition));
            mainPanel.getLayoutParams().height = mainPanel.getHeight() - (Y - previousFingerPosition);
            mainPanel.requestLayout();
          }

          // Update position
          previousFingerPosition = Y;
        }
        break;
    }
    return true;
  }

  public void closeUpAndDismissDialog(int currentPosition) {
    isClosing = true;
    ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(mainPanel, "y", currentPosition, -mainPanel.getHeight());
    positionAnimator.setDuration(300);
    positionAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animator) {
        dismiss();
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    positionAnimator.start();
  }

  public void closeDownAndDismissDialog(int currentPosition) {
    isClosing = true;
    Display display = getActivity().getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int screenHeight = size.y;
    ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(mainPanel, "y", currentPosition, screenHeight + mainPanel.getHeight());
    if (currentPosition > 200) positionAnimator.setDuration(600);
    else positionAnimator.setDuration(300);
    positionAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animator) {
        dismiss();
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    positionAnimator.start();
  }
}
