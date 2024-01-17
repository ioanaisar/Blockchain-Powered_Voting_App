package ro.pub.cs.systems.eim.votingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VotingPage extends AppCompatActivity {

    User user = null;
    User admin = null;

    String contractAddress = null;

    VotingAdapter votingAdapter;

    Pair<Integer, String> selectedVote = null;

    private SetVoteThread setValueContractThread = null;

    String ganacheUrl = null;

    String topic = null;


    private Handler handlerSetValueContract = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String valueFromThread = bundle.getString("CONTRACT_RECEIPT");
            Log.d("HANDLER", "Contract receipt: " + valueFromThread);
            finish();
            return false;
        }
    });


    private final SetVoteContractButtonClickListener setVoteContractButtonClickListener = new SetVoteContractButtonClickListener();

    private class SetVoteContractButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            selectedVote = votingAdapter.getSelectedVote();
            Log.d("VotingAdapter", "Check vote ");
            if (selectedVote != null) {
                Toast.makeText(getApplicationContext(), "Vote registered!", Toast.LENGTH_SHORT).show();
                Log.d("VotingAdapter", "Voted " + selectedVote.second);

                setValueContractThread = new SetVoteThread(ganacheUrl, user, handlerSetValueContract, admin, contractAddress, topic, selectedVote.second);
                setValueContractThread.start();
            } else {
                Log.d("VotingAdapter", "No vote selected");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);

        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("USER");
            admin = (User) intent.getSerializableExtra("ADMIN");
            contractAddress = intent.getStringExtra("CONTRACT_ADDRESS");
            ganacheUrl = intent.getStringExtra("GANACHE_URL");
            topic = intent.getStringExtra("TOPIC");
        }

        // makes a list of voting options
        List<Pair<Integer, String>> options = new ArrayList<>();
        options.add(new Pair<>(1, "Londra"));
        options.add(new Pair<>(2, "Paris"));
        options.add(new Pair<>(3, "Roma"));
        options.add(new Pair<>(4, "Tokyo"));

        votingAdapter = new VotingAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(votingAdapter);


        // button submit vote
        Button setValueContractButton = findViewById(R.id.submitVoteButton);
        setValueContractButton.setOnClickListener(setVoteContractButtonClickListener);

    }
}