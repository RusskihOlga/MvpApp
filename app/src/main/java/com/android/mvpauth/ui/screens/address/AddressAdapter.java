package com.android.mvpauth.ui.screens.address;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private ArrayList<UserAddressDTO> mUserAddress = new ArrayList<>();

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private Handler handler = new Handler();

    public void addItem(UserAddressDTO address) {
        mUserAddress.add(address);
        notifyDataSetChanged();
    }

    public void reloadAdapter() {
        mUserAddress.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mEditAddress.setHint(mUserAddress.get(position).getName());
        holder.mEditAddress.setText(addressToString(mUserAddress.get(position)));
        holder.mEditComment.setText(mUserAddress.get(position).getComment());
    }

    public void remove(int position) {
        mUserAddress.remove(position);
        notifyItemRemoved(position);
    }

    private String addressToString(UserAddressDTO userAddressDTO) {
        return "ул. " +
                userAddressDTO.getStreet() +
                " " +
                userAddressDTO.getHouse() +
                "-" +
                userAddressDTO.getApartment() +
                ", " +
                userAddressDTO.getFloor() +
                " этах";
    }

    @Override
    public int getItemCount() {
        return mUserAddress.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.edit_address)
        EditText mEditAddress;
        @BindView(R.id.edit_comment)
        EditText mEditComment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
