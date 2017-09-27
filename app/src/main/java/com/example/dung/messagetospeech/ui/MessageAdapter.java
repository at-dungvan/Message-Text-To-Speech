package com.example.dung.messagetospeech.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dung.messagetospeech.R;
import com.example.dung.messagetospeech.models.Message;

import java.util.List;

/**
 *
 * Created by dung on 29/08/2017.
 *
 */
class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private List<Message> mMessages;

    MessageAdapter(List<Message> messages, OnItemClickListener onItemClickListener) {
        mMessages = messages;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_message, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        holder.mTvMessage.setText(mMessages.get(position).getMessage());
        holder.mTvContact.setText(mMessages.get(position).getSender());
    }

    @Override
    public int getItemCount() {
        if (mMessages == null) {
            return 0;
        } else {
            return mMessages.size();
        }
    }

    /**
     * Class ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvContact;
        private TextView mTvMessage;
        private ImageButton mImgBtnSpeak;

        ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
            // event click item
            mImgBtnSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
        }

        private void initView(View item) {
            mTvMessage = item.findViewById(R.id.tvItemMesssage);
            mTvContact = item.findViewById(R.id.tvItemContact);
            mImgBtnSpeak = item.findViewById(R.id.imgBtnSpeak);
        }
    }

    /**
     * Callback click item.
     */
    interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
