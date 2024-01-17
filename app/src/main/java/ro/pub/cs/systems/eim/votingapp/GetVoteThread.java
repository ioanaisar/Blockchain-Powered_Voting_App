package ro.pub.cs.systems.eim.votingapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetVoteThread extends Thread {
    private static final String TAG = "GetVoteContractThread";

    private final String ganacheUrl;

    private final User currentUser;

    private Handler handler;

    private final User admin;

    private final String contractAddress;

    private final String topic;

    public GetVoteThread(String ganacheUrl, User currentUser, Handler handler, User admin, String contractAddress, String topic) {
        this.ganacheUrl = ganacheUrl;
        this.currentUser = currentUser;
        this.handler = handler;
        this.admin = admin;
        this.contractAddress = contractAddress;
        this.topic = topic;
    }

    @Override
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(ganacheUrl));

        try {
            Credentials credentials = Credentials.create(admin.getPrivateKey());

            GanacheGasProvider ganacheGasProvider = new GanacheGasProvider(BigInteger.valueOf(20000000000L), BigInteger.valueOf(3000000L));

            List<byte[]> votes = VotingInfo.load(contractAddress, web3j, credentials, ganacheGasProvider).getTopicVotes(topic).send();

            ArrayList<String> votesString = new ArrayList<>();
            for (byte[] b : votes) {
                votesString.add(new String(b, StandardCharsets.UTF_8).trim());
            }

            Log.d(TAG, "Votes : " + votes.toString());
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("VOTE_LIST", (ArrayList<String>) votesString);
            message.setData(bundle);
            handler.sendMessage(message);

        } catch (IOException e) {
            Log.e(TAG, "Error fetching votes from contract: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void stopThread() {
        interrupt();
    }
}

