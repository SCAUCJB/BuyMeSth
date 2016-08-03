package adpater.animation;

import android.animation.Animator;
import android.view.View;

/**
 * Created by John on 2016/8/3.
 */

public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
