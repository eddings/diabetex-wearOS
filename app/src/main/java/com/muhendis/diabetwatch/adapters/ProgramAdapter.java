package com.muhendis.diabetwatch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.ExerciseActivity;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {
    private ProgramFirebaseDb[] mDataset;
    public Activity activity;
    private LocalDBHelper mLocalDbHelper;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ProgramViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public String pid;
        private final ImageView mProgramDoneImage;

        public ProgramViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.program_recyler_view_text);
            mProgramDoneImage = v.findViewById(R.id.programDoneImage);
        }
    }

    public ProgramAdapter(Activity activity) {
        this.activity = activity;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ProgramAdapter(ProgramFirebaseDb[] myDataset,Activity activity) {
        mDataset = myDataset;
        this.activity = activity;
    }

    public void setAdapter(ProgramFirebaseDb[] myDataset){
        mDataset = myDataset;
        notifyDataSetChanged();
    }
    public void setLocalDbHelper(LocalDBHelper mLocalDbHelper){
        this.mLocalDbHelper = mLocalDbHelper;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ProgramAdapter.ProgramViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_recyler_view_item, parent, false);

        final ProgramAdapter.ProgramViewHolder vh = new ProgramAdapter.ProgramViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ExerciseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(activity.getResources().getString(R.string.intent_key_pid),vh.pid);
                activity.startActivity(intent);
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(mLocalDbHelper.isStatisticsProgramInsertedToday(mDataset[position].getPid()))
        {
            holder.mProgramDoneImage.setVisibility(View.VISIBLE);
        }
        holder.mTextView.setText(mDataset[position].getName());
        holder.pid = mDataset[position].getPid();

        if(position%2==0)
            holder.itemView.setBackgroundColor(activity.getColor(R.color.action_button_background));
        else
            holder.itemView.setBackgroundColor(activity.getColor(R.color.grey));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset!= null)
            return mDataset.length;
        else
            return 0;
    }
}