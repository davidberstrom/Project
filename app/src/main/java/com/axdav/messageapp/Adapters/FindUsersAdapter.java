package com.axdav.messageapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.axdav.messageapp.Model.User;
import com.axdav.messageapp.R;
import com.axdav.messageapp.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class FindUsersAdapter extends RecyclerView.Adapter<FindUsersAdapter.ViewHolder> {
    private Context context;
    private List<User> users;
    private User user;
    final private myClickListener myClickListener = new myClickListener();

    public FindUsersAdapter(Context context,List<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.find_user_item,parent,false);
        Button b = view.findViewById(R.id.profile_btn);
        b.setOnClickListener(myClickListener);
        return new FindUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user = users.get(position);
       holder.username.setText(user.getUsername());
       holder.userId.setText(user.getUserId());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView userId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_View);
            userId = itemView.findViewById(R.id.findUserItem_userId);
         }
    }

    private class myClickListener implements View.OnClickListener{

        @Override
        public void onClick(@NonNull View view) {
            RelativeLayout rel = (RelativeLayout)view.getParent();
            TextView tv = rel.findViewById(R.id.username_View);
            TextView id = rel.findViewById(R.id.findUserItem_userId);
            String extraId = id.getText().toString();
            String extra = tv.getText().toString();
            Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
            intent.putExtra("ID",extraId);
            intent.putExtra("NAME", extra);
            view.getContext().startActivity(intent);
        }
    }
}
