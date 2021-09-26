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

public class SubjectRecyclerAdapter extends RecyclerView.Adapter<SubjectRecyclerAdapter.SubjectViewHolder> {

    private final LayoutInflater inflater;
    private final List<Subject> subjects;

    @NonNull
    @NotNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.subject_list_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.name.setText(subject.getName());
        holder.authorLogin.setText(subject.getAuthorLogin());
        holder.totalSum.setText(Double.toString(subject.getTotalSum()));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public SubjectRecyclerAdapter(Context context, List<Subject> subjects) {
        this.inflater = LayoutInflater.from(context);
        this.subjects = subjects;
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView authorLogin;
        private final TextView totalSum;



        SubjectViewHolder(View viewItem) {
            super(viewItem);

            this.name = viewItem.findViewById(R.id.subjectItemSubjectTextView);
            this.authorLogin = viewItem.findViewById(R.id.subjectItemLogintTextView);
            this.totalSum = viewItem.findViewById(R.id.subjectItemSumTextView);
        }
    }
}
