package com.android.mvpauth.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.mvpauth.R;
import com.android.mvpauth.mortar.ScreenScoper;
import com.android.mvpauth.utils.ViewHelper;

import java.util.Collections;
import java.util.Map;

import flow.Direction;
import flow.Dispatcher;
import flow.KeyChanger;
import flow.State;
import flow.Traversal;
import flow.TraversalCallback;
import flow.TreeKey;

public class TreeKeyDispatcher extends KeyChanger implements Dispatcher {
    private Activity mActivity;
    private Object inKey;
    @Nullable
    private Object outKey;
    private FrameLayout mRootFrame;

    public TreeKeyDispatcher(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void dispatch(Traversal traversal, TraversalCallback callback) {
        Map<Object, Context> contexts;
        State inState = traversal.getState(traversal.destination.top());
        inKey = inState.getKey();
        State outState = traversal.origin == null ? null : traversal.getState(traversal.origin.top());
        outKey = outState == null ? null : outState.getKey();

        mRootFrame = (FrameLayout) mActivity.findViewById(R.id.root_frame);

        if (inKey.equals(outKey)) {
            callback.onTraversalCompleted();
            return;
        }

        if (inKey instanceof TreeKey) {
            // TODO: 02.12.2016 implement treekey case
        }

        Context flowContext = traversal.createContext(inKey, mActivity);
        Context mortarContext = ScreenScoper.getScreenScope((AbstractScreen) inKey).createContext(flowContext);
        contexts = Collections.singletonMap(inKey, mortarContext);
        changeKey(outState, inState, traversal.direction, contexts, callback);
    }

    @Override
    public void changeKey(@Nullable State outgoingState, State incomingState, Direction direction, Map<Object, Context> incomingContexts, TraversalCallback callback) {
        Context context = incomingContexts.get(inKey);
        //save prev view

        if (outgoingState != null) {
            outgoingState.save(mRootFrame.getChildAt(0));
        }

        //create view
        Screen screen;
        screen = inKey.getClass().getAnnotation(Screen.class);
        if (screen == null) {
            throw new IllegalStateException("@Screen annotation is missing on screen " + ((AbstractScreen) inKey).getScopeName());
        } else {
            int layout = screen.value();

            LayoutInflater inflater = LayoutInflater.from(context);
            View newView = inflater.inflate(layout, mRootFrame, false);
            View oldView = mRootFrame.getChildAt(0);
            //restore state to new view
            incomingState.restore(newView);

            // TODO: 02.12.2016 unregister screen scope
            //delete old view
            /*if (outKey != null && !(inKey instanceof TreeKey)) {
                ((AbstractScreen) outKey).unregisterScope();
            }*/

            /*if (mRootFrame.getChildAt(0) != null) {
                mRootFrame.removeView(mRootFrame.getChildAt(0));
            }*/

            mRootFrame.addView(newView);

            ViewHelper.waitForMeasure(newView, (view, width, height) -> runAnimation(mRootFrame, oldView, newView, direction, new TraversalCallback(){
                @Override
                public void onTraversalCompleted() {
                    //delete old view
                    if ((outKey) !=null && !(inKey instanceof  TreeKey)) {
                        ((AbstractScreen) outKey).unregisterScope();
                    }
                    callback.onTraversalCompleted();
                }
            }));
        }
    }

    private void runAnimation(FrameLayout rootFrame, View from, View to, Direction direction, TraversalCallback traversalCallback) {
        Animator animator = createAnimation(from, to, direction);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (from != null) {
                    rootFrame.removeView(from);
                }
                traversalCallback.onTraversalCompleted();
            }
        });

        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.start();
    }

    private Animator createAnimation(@Nullable View from, View to, Direction direction) {
        boolean backward = direction == Direction.BACKWARD;

        AnimatorSet set = new AnimatorSet();

        int fromTranslation;
        if (from != null) {
            fromTranslation = backward ? from.getWidth() : -from.getWidth();
            final ObjectAnimator outAnimator = ObjectAnimator.ofFloat(from, "translationX", fromTranslation);
            set.play(outAnimator);
        }

        int toTranslation = backward ? -to.getWidth() : to.getWidth();
        final ObjectAnimator toAnimation = ObjectAnimator.ofFloat(to, "translationX", toTranslation, 0);
        set.play(toAnimation);

        return set;
    }
}
