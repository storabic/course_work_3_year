package com.cw.androidclient.ui.login;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cw.androidclient.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ClientViewHolder> {

    private final LayoutInflater inflater;
    private final List<Client> clients;

    @NonNull
    @NotNull
    @Override
    public PaymentRecyclerAdapter.ClientViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.payment_list_item, parent, false);
        return new PaymentRecyclerAdapter.ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PaymentRecyclerAdapter.ClientViewHolder holder, int position) {
        Client subject = clients.get(position);
        holder.login.setText(subject.getLogin());
        holder.setClientId(subject.getClientId());
        holder.sum.setText(String.valueOf(subject.getSum()));
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public PaymentRecyclerAdapter(Context context, List<Client> clients) {
        this.inflater = LayoutInflater.from(context);
        this.clients = clients;
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        private final TextView login;
        private final TextView active;
        private final TextView sum;
        private long clientId;
        private long paymentId;

        public long getClientId() {
            return clientId;
        }

        public void setClientId(long clientId) {
            this.clientId = clientId;
        }

        ClientViewHolder(View viewItem) {
            super(viewItem);

            this.login = viewItem.findViewById(R.id.textView6);
            this.active = viewItem.findViewById(R.id.textView10);
            this.sum = viewItem.findViewById(R.id.textView9);
        }

        public long getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(long paymentId) {
            this.paymentId = paymentId;
        }
    }
}
