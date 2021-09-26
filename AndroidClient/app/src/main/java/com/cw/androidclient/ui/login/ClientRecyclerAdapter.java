package com.cw.androidclient.ui.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cw.androidclient.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClientRecyclerAdapter extends RecyclerView.Adapter<ClientRecyclerAdapter.ClientViewHolder> {

    private final LayoutInflater inflater;
    private final List<Client> clients;

    @NonNull
    @NotNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.client_list_item, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClientViewHolder holder, int position) {
        Client subject = clients.get(position);
        holder.login.setText(subject.getLogin());
        holder.setClientId(subject.getClientId());
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public ClientRecyclerAdapter(Context context, List<Client> clients) {
        this.inflater = LayoutInflater.from(context);
        this.clients = clients;
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        private final TextView login;
        private final CheckBox checkBox;
        private long clientId;

        public boolean isChecked() {
            return checkBox.isChecked();
        }

        public long getClientId() {
            return clientId;
        }

        public void setClientId(long clientId) {
            this.clientId = clientId;
        }

        ClientViewHolder(View viewItem) {
            super(viewItem);

            this.login = viewItem.findViewById(R.id.clientItemSubjectTextView);
            this.checkBox = viewItem.findViewById(R.id.checkBox);
        }
    }
}
