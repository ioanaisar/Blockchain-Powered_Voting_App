package ro.pub.cs.systems.eim.votingapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class SetVoteThread extends Thread {
    private static final String TAG = "SetValueContractThread";

    private final String ganacheUrl;

    private final User currentUser;

    private Handler handler;

    private final User admin;

    private final String contractAddress;

    private final String topic;

    private final String vote;

    public SetVoteThread(String ganacheUrl, User currentUser, Handler handler, User admin, String contractAddress, String topic, String vote) {
        this.ganacheUrl = ganacheUrl;
        this.currentUser = currentUser;
        this.handler = handler;
        this.admin = admin;
        this.contractAddress = contractAddress;
        this.topic = topic;
        this.vote = vote;

    }

    @Override
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(ganacheUrl));

        try {
            Credentials credentials = Credentials.create(admin.getPrivateKey());
            GanacheGasProvider ganacheGasProvider = new GanacheGasProvider(BigInteger.valueOf(20000000000L), BigInteger.valueOf(3000000L));

            TransactionReceipt transactionReceipt = null;
            try {

                byte[] bytes = vote.getBytes();
                byte[] bytes32 = new byte[32];
                System.arraycopy(bytes, 0, bytes32, 0, bytes.length);
                transactionReceipt = VotingInfo.load(contractAddress, web3j, credentials, ganacheGasProvider).setVotes(topic, currentUser.getUsername(), vote, bytes32).send();
                if (transactionReceipt != null) {
                    System.out.println("Transaction receipt: " + transactionReceipt.getLogs());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error contract set vote: " + e.getMessage());
            }

            if (transactionReceipt != null) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("CONTRACT_STATUS", transactionReceipt.getStatus());
                message.setData(bundle);
                handler.sendMessage(message);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error contract add vote: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void stopThread() {
        interrupt();
    }
}
