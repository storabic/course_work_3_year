package com.cw.androidclient.ui.login;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Клиент в платеже, при создании каждого нового платежа должен создаваться новый, т.к. хранит сумму платы
 */
public class Client implements Serializable, Parcelable {

    private final long clientId;

    private final String login;

    private double sum;

    private String closedDate;

    private String comment;

    private long paymentId;

    public Client(long clientId, String login) {
        this.clientId = clientId;
        this.login = login;
    }

    public Client(long clientId, String login, double sum) {
        this(clientId, login);
        this.sum = sum;
    }

    protected Client(Parcel in) {
        clientId = in.readLong();
        login = in.readString();
        sum = in.readDouble();
    }

    public long getClientId() {
        return clientId;
    }

    public String getLogin() {
        return login;
    }

    public double getSum() {
        return sum;
    }

    public void setSum() {
        this.sum = sum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(clientId);
        parcel.writeString(login);
        parcel.writeDouble(sum);
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public String getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(String closedDate) {
        this.closedDate = closedDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }
}
