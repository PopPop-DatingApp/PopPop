package com.example.poppop.cardstackview.internal;

import android.view.animation.Interpolator;

import com.example.poppop.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
