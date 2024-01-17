package ro.pub.cs.systems.eim.votingapp;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VotingAdapter extends RecyclerView.Adapter<VotingAdapter.ViewHolder> {
    private List<Pair<Integer, String>> votes;

    private Pair selectedVote = new Pair(-1, "Empty");

    public VotingAdapter(List<Pair<Integer, String>> votes) {
        this.votes = votes;
    }

    @NonNull
    @Override
    public VotingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_votes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VotingAdapter.ViewHolder holder, int position) {
        Pair<Integer, String> vote = votes.get(position);
        holder.voteButton.setChecked(vote.first == selectedVote.first);
        holder.voteText.setText(vote.second);
    }


    @Override
    public int getItemCount() {
        return votes.size();
    }

    public Pair<Integer, String> getSelectedVote() {
        Log.d("VotingAdapter", "Selected Votes: " + selectedVote.first + ", " + selectedVote.second);
        return selectedVote;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView voteText;
        RadioButton voteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            voteText = itemView.findViewById(R.id.voteText);
            voteButton = itemView.findViewById(R.id.voteButton);

            View.OnClickListener clickListener = v -> {
                int position = getAdapterPosition();
                selectedVote = votes.get(position);
                notifyDataSetChanged();
            };

            itemView.setOnClickListener(clickListener);
            voteButton.setOnClickListener(clickListener);
        }

    }
}
