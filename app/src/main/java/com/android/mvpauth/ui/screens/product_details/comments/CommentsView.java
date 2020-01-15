package com.android.mvpauth.ui.screens.product_details.comments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;

import butterknife.BindView;

public class CommentsView extends AbstractView<CommentScreen.CommentsPresenter> {

    private CommentsAdapter mAdapter = new CommentsAdapter();
    @BindView(R.id.list_comment)
    RecyclerView mListComments;

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CommentScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public CommentsAdapter getAdapter() {
        return mAdapter;
    }

    public void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mListComments.setLayoutManager(llm);
        mListComments.setAdapter(mAdapter);
    }

    public void showAddCommentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View dialogView = inflater.inflate(R.layout.add_comment, null);

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) dialogView.findViewById(R.id.comment_rb);
        EditText commentEt = (EditText) dialogView.findViewById(R.id.comment_et);

        dialogBuilder.setTitle("Оставьть отзыв о товаре?")
                .setView(dialogView)
                .setPositiveButton("Оставить отзыв", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommentRealm commentRealm = new CommentRealm(ratingBar.getRating(), commentEt.getText().toString());
                        mPresenter.addComment(commentRealm);
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                .show();
    }
}
