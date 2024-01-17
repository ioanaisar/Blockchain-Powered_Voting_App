package ro.pub.cs.systems.eim.votingapp;

import org.web3j.protocol.Web3j;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;


public class ConnectionThread extends Thread {

    private static final String TAG = "BalanceRetrievalThread";
    private final String ganacheUrl;

    private final User currentUser;

    private Handler handler;

    public ConnectionThread(String ganacheUrl, User currentUser, Handler handler) {
        this.ganacheUrl = ganacheUrl;
        this.currentUser = currentUser;
        this.handler = handler;
    }

    @Override
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(ganacheUrl));

        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(currentUser.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger balanceInWei = ethGetBalance.getBalance();
            BigDecimal balanceInEther = Convert.fromWei(balanceInWei.toString(), Convert.Unit.ETHER);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("VALUE_KEY", balanceInEther.toString());
            message.setData(bundle);
            handler.sendMessage(message);

        } catch (IOException e) {
            Log.e(TAG, "Error fetching balance for account : " + e.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
    }


}
