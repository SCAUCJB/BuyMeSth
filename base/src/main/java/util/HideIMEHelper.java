package util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

/**
 * Created by John on 2016/9/21.
 *用法：HideIMEHelper.wrap(this)就可以了
 */

public class HideIMEHelper extends FrameLayout {
    public static void wrap(Activity activity) {
        ViewGroup contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
        wrap(contentParent);
    }

    public static void wrap(Fragment fragment) {
        ViewGroup contentParent = (ViewGroup) fragment.getView().getParent();
        wrap(contentParent);
    }

    public static void wrap(ViewGroup contentParent) {
        View content = contentParent.getChildAt(0);
        contentParent.removeView(content);

        ViewGroup.LayoutParams p = content.getLayoutParams();
        HideIMEHelper layout = new HideIMEHelper(content.getContext());
        layout.addView(content);

        contentParent.addView(layout, new ViewGroup.LayoutParams(p.width, p.height));
    }

    public HideIMEHelper(Context context) {
        super(context);
    }

    public HideIMEHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HideIMEHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Context context = getContext();
            if (context == null || !(context instanceof Activity)) {
                return super.dispatchTouchEvent(ev);
            }

            Activity activity = (Activity) context;
            View focusView = activity.getCurrentFocus();

            if (focusView != null && shouldHideInputMethod(focusView, ev)) {
                hideInputMethod(focusView);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean shouldHideInputMethod(View focusView, MotionEvent event) {
        Rect rect = new Rect();
        focusView.getHitRect(rect);
        if (rect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        return true;
    }

    private void hideInputMethod(View currentFocus) {
        if (currentFocus == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) currentFocus.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
