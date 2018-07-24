package com.muhendis.diabetwatch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.ExerciseActivity;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;

public class ProgramWearableAdapter extends WearableRecyclerView.Adapter<ProgramWearableAdapter.ProgramViewHolder> {
    private ProgramFirebaseDb[] mDataset;
    public Activity activity;
    private LocalDBHelper mLocalDbHelper;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ProgramViewHolder extends WearableRecyclerView.ViewHolder {
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

    public ProgramWearableAdapter(Activity activity) {
        this.activity = activity;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ProgramWearableAdapter(ProgramFirebaseDb[] myDataset,Activity activity) {
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
    public ProgramWearableAdapter.ProgramViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_recyler_view_item, parent, false);

        final ProgramViewHolder vh = new ProgramViewHolder(v);

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
        if(mLocalDbHelper.isStatisticsProgramInsertedToday(mDataset[position].getPid()))
        {
            holder.mProgramDoneImage.setVisibility(View.VISIBLE);
        }
        holder.mTextView.setText(mDataset[position].getName());
        holder.pid = mDataset[position].getPid();

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