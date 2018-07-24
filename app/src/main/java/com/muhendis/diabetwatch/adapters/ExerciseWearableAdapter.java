package com.muhendis.diabetwatch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.ExerciseActivity;
import com.muhendis.diabetwatch.activities.ExerciseDetailsActivity;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.model.ProgramExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;

public class ExerciseWearableAdapter extends WearableRecyclerView.Adapter<ExerciseWearableAdapter.ExerciseViewHolder> {
    private ProgramExerciseFirebaseDb[] mDataset;
    public Activity activity;
    private LocalDBHelper mLocalDbHelper;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ExerciseViewHolder extends WearableRecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mExName;
        public String pid;
        private final ImageView exDoneTick;

        public ExerciseViewHolder(View v) {
            super(v);
            mExName = v.findViewById(R.id.exercise_recyler_view_text);
            exDoneTick = v.findViewById(R.id.exerciseTickImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExerciseWearableAdapter(ProgramExerciseFirebaseDb[] myDataset, Activity activity) {
        mDataset = myDataset;
        this.activity = activity;
    }

    public void setAdapter(ProgramExerciseFirebaseDb[] myDataset){
        mDataset = myDataset;
        notifyDataSetChanged();
    }
    public void setLocalDbHelper(LocalDBHelper mLocalDbHelper){
        this.mLocalDbHelper = mLocalDbHelper;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ExerciseWearableAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_recyler_view_item, parent, false);

        final ExerciseViewHolder vh = new ExerciseViewHolder(v);

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
    public void onBindViewHolder(ExerciseViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(mLocalDbHelper.isExerciseFinishedToday(mDataset[position].getEid(),mDataset[position].getPid()))
        {
            holder.exDoneTick.setVisibility(View.VISIBLE);
        }
        else{
            holder.exDoneTick.setVisibility(View.GONE);
        }
        holder.mExName.setText(mDataset[position].getName());
        holder.pid = mDataset[position].getPid();
        /*if(position%2==0)
            holder.itemView.setBackgroundColor(activity.getColor(R.color.action_button_background));
        else
            holder.itemView.setBackgroundColor(activity.getColor(R.color.grey));*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExerciseDetailsActivity.class);
                intent.putExtra(Keys.EX_ID,mDataset[position].getEid());
                intent.putExtra(Keys.PID_KEY,mDataset[position].getPid());
                //v.getContext().startActivity(intent);
                activity.startActivityForResult(intent,0);
            }
        });

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