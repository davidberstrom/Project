package com.axdav.messageapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axdav.messageapp.Model.User;
import com.axdav.messageapp.R;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<User> friendReqList;
    private User friendReqUser;

    public NotificationAdapter(Context context, List<User>friendReqList){
        this.context = context;
        this.friendReqList = friendReqList;
    }

    public NotificationAdapter(List<User>friendReqList){
        this.friendReqList = friendReqList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }


    public void onBindViewHolder(ViewHolder holder, int position) {
        friendReqUser = friendReqList.get(position);
        holder.username_notification.setText(friendReqUser.getUsername());
    }

    @Override
    public int getItemCount() {
        return friendReqList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username_notification;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username_notification = itemView.findViewById(R.id.username_view);
        }
    }
}
