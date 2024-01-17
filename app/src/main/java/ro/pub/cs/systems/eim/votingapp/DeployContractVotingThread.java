package ro.pub.cs.systems.eim.votingapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class DeployContractVotingThread extends Thread {

    private static final String TAG = "ContractThread";

    private final String ganacheUrl;

    private final User currentUser;

    private Handler handler;

    private final User admin;

    public DeployContractVotingThread(String ganacheUrl, User currentUser, Handler handler, User admin) {
        this.ganacheUrl = ganacheUrl;
        this.currentUser = currentUser;
        this.handler = handler;
        this.admin = admin;
    }

    @Override
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(ganacheUrl));

        try {

            // deploy contract
            BigInteger gasLimit = BigInteger.valueOf(3000000L);
            Credentials credentials = Credentials.create(admin.getPrivateKey());

            GanacheGasProvider ganacheGasProvider = new GanacheGasProvider(BigInteger.valueOf(20000000000L), gasLimit);
            VotingInfo contract = VotingInfo.deploy(web3j, credentials, ganacheGasProvider).send();

            String contractAddress = contract.getContractAddress();

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("CONTRACT_ADDRESS", contractAddress);
            message.setData(bundle);
            handler.sendMessage(message);

        } catch (IOException e) {
            Log.e(TAG, "Error fetching balance for current user: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopThread() {
        interrupt();
    }

}
