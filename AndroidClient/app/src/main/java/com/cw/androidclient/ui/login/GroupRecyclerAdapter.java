package com.cw.androidclient.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cw.androidclient.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder> {

    private final LayoutInflater inflater;
    private final List<Group> groups;

    @NonNull
    @NotNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.name.setText(group.getName());
        holder.token.setText(group.getToken());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public GroupRecyclerAdapter(Context context, List<Group> groups) {
        this.inflater = LayoutInflater.from(context);
        this.groups = groups;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView token;

        GroupViewHolder(View viewItem) {
            super(viewItem);

            this.name = viewItem.findViewById(R.id.textView66);
            this.token = viewItem.findViewById(R.id.textView99);
        }
    }
}
