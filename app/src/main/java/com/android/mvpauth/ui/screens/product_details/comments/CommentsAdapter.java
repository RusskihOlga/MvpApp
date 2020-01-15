package com.android.mvpauth.ui.screens.product_details.comments;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.CommentDto;
import com.android.mvpauth.di.DaggerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>{

    private List<CommentDto> mCommentsList = new ArrayList<>();
    @Inject
    Picasso mPicasso;

    public void addItem(CommentDto commentDto) {
        mCommentsList.add(commentDto);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<CommentScreen.Component>getDaggerComponent(recyclerView.getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        CommentDto comment = mCommentsList.get(position);
        holder.mNameUser.setText(comment.getUserName());
        holder.mTime.setText(comment.getCommentDate());
        holder.mRating.setRating(comment.getRating());
        holder.mComment.setText(comment.getComment());
        String urlAvatar = comment.getAvatarUrl();
        if (urlAvatar == null || urlAvatar.isEmpty()) {
            urlAvatar = "http://skill-branch.ru/img/app/avatar-1.png";
        }
        mPicasso.load(urlAvatar)
                .error(R.drawable.ic_account_circle_black_24dp)
                .fit()
                .into(holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    public void reloadAdapter(List<CommentDto> commentDtos) {
        mCommentsList.clear();
        mCommentsList = commentDtos;
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        ImageView mAvatar;
        @BindView(R.id.name_user)
        TextView mNameUser;
        @BindView(R.id.rating)
        AppCompatRatingBar mRating;
        @BindView(R.id.time)
        TextView mTime;
        @BindView(R.id.comment)
        TextView mComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
