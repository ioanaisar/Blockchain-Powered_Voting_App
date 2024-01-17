package ro.pub.cs.systems.eim.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import ro.pub.cs.systems.eim.votingapp.constants.Constants;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity {

    String ganacheUrl = Constants.GANACHE_URL;
    private ConnectionThread serverThread = null;

    private PaymentThread paymentThread = null;

    private DeployContractVotingThread contractThread = null;

    private GetVoteThread getValueContractThread = null;


    private String contractAddress = null;

    Button getBalanceButton = null;

    Button payButton = null;

    String topic = "Jocurile Olimpice";

    User user = null;

    ArrayList<String> votes = new ArrayList<>();
    User admin  = new User("admin", "0x83299dfA39eef6582DbDfB525f0D1d0c8B22Be86",
            "admin", "0x9b3ae1c6547219f12072441e404996436223a6aa3b4fe1bafdbe415f90e7b143", "admin.json");

    TextView balanceTextView = null;


    TextView voteTextView = null;

    private Handler handlerBalance = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String valueFromThread = bundle.getString("VALUE_KEY");

            Log.d("HANDLER", "Value received from Thread: " + valueFromThread);
            balanceTextView.setText("Balance:\t" + valueFromThread);
            return false;
        }
    });

    private Handler handlerPayment = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String valueFromThread = bundle.getString("VALUE_KEY");

            Log.d("HANDLER", "Value received from Thread: " + valueFromThread);
            return false;
        }
    });

    private Handler handlerDeployVotingContract = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String valueFromThread = bundle.getString("CONTRACT_ADDRESS");

            Log.d("HANDLER", "Deploy Contract: " + valueFromThread);
            contractAddress = valueFromThread;
            return false;
        }
    });

    private Handler handlerGetValueContract = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            votes = bundle.getStringArrayList("VOTE_LIST");
            String votesString = votes.toString();

            Log.d("Handler", "Votes: " + votesString);
            voteTextView.setText("Voted with:\t" +  votesString);
            return false;
        }
    });

    private final GetBalanceButtonClickListener balanceButtonClickListener = new GetBalanceButtonClickListener();

    private class GetBalanceButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            serverThread = new ConnectionThread(ganacheUrl, user, handlerBalance);
            serverThread.start();

        }
    }


    private final PaymentButtonClickListener paymentButtonClickListener = new PaymentButtonClickListener();

    private class PaymentButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            paymentThread = new PaymentThread(ganacheUrl, user, handlerPayment, admin);
            paymentThread.start();

        }
    }


    private final GetValueContractButtonClickListener getValueContractButtonClickListener = new GetValueContractButtonClickListener();

    private class GetValueContractButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            getValueContractThread = new GetVoteThread(ganacheUrl, user, handlerGetValueContract, admin, contractAddress, topic);
            getValueContractThread.start();
        }
    }



    private final GoToVotingButtonClickListener goToVotingButtonClickListener = new GoToVotingButtonClickListener();

    private class GoToVotingButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d("VotingAdapter", "will send intent: " + user.getUsername().toString()+ " " + admin.getUsername());
            Intent intent = new Intent(OptionsActivity.this, VotingPage.class);
            intent.putExtra("USER", user);
            intent.putExtra("ADMIN", admin);
            intent.putExtra("CONTRACT_ADDRESS", contractAddress);
            intent.putExtra("GANACHE_URL", ganacheUrl);
            intent.putExtra("TOPIC", topic);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("USER");
        }

        // button getBalance
        getBalanceButton = findViewById(R.id.balance);
        getBalanceButton.setOnClickListener(balanceButtonClickListener);

        // button payment
        payButton = findViewById(R.id.getMoney);
        payButton.setOnClickListener(paymentButtonClickListener);

        // button get value contract
        Button getValueContractButton = findViewById(R.id.getValueContract);
        getValueContractButton.setOnClickListener(getValueContractButtonClickListener);

        // buton new page for voting
        Button votingButton = findViewById(R.id.GetToVotingPage);
        votingButton.setOnClickListener(goToVotingButtonClickListener);

        // text balance result
        balanceTextView = (TextView) findViewById(R.id.balanceText);

        //  vote result from contract
        voteTextView = (TextView) findViewById(R.id.contractVote);

        contractThread = new DeployContractVotingThread(ganacheUrl, user, handlerDeployVotingContract, admin);
        contractThread.start();

    }

    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }

        if (paymentThread != null) {
            paymentThread.stopThread();
        }

        if (contractThread != null) {
            contractThread.stopThread();
        }

        if (getValueContractThread != null) {
            getValueContractThread.stopThread();
        }

        super.onDestroy();
    }

}