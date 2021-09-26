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
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class UserPayRecyclerAdapter extends RecyclerView.Adapter<UserPayRecyclerAdapter.ClientViewHolder> {

    private final LayoutInflater inflater;
    private final List<Client> clients;

    @NonNull
    @NotNull
    @Override
    public UserPayRecyclerAdapter.ClientViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_pay_item, parent, false);
        return new UserPayRecyclerAdapter.ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserPayRecyclerAdapter.ClientViewHolder holder, int position) {
        Client subject = clients.get(position);
        holder.login.setText(subject.getLogin());
        holder.setClientId(subject.getClientId());
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public UserPayRecyclerAdapter(Context context, List<Client> clients) {
        this.inflater = LayoutInflater.from(context);
        this.clients = clients;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        private final TextView login;
        private final TextInputEditText inputEditText;
        private long clientId;

        public double getSum() {
            double x = Double.parseDouble(inputEditText.getEditableText().toString());
            if (x <= 0) {
                throw new NumberFormatException();
            }
            return round(x, 2);
        }

        public long getClientId() {
            return clientId;
        }

        public void setClientId(long clientId) {
            this.clientId = clientId;
        }

        ClientViewHolder(View viewItem) {
            super(viewItem);

            this.login = viewItem.findViewById(R.id.userPayItemSubjectTextView);
            this.inputEditText = viewItem.findViewById(R.id.abpayinput);
        }
    }
}
