package ro.pub.cs.systems.eim.votingapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class PaymentThread extends Thread {
    private static final String TAG = "PaymentThread";

    private final String ganacheUrl;

    private final User currentUser;

    private Handler handler;

    private final User admin;

    public PaymentThread(String ganacheUrl, User currentUser, Handler handler, User admin) {
        this.ganacheUrl = ganacheUrl;
        this.currentUser = currentUser;
        this.handler = handler;
        this.admin = admin;
    }

    @Override
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(ganacheUrl));

        try {

            BigInteger valueInWei = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();
            Credentials credentials = Credentials.create(admin.getPrivateKey());

            BigInteger gasPrice = BigInteger.valueOf(20000000000L);
            BigInteger gasLimit = BigInteger.valueOf(6721975L);

            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    credentials.getAddress(), DefaultBlockParameterName.LATEST).send();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, gasLimit, currentUser.getAddress(), valueInWei);

            byte[] message = TransactionEncoder.signMessage(rawTransaction, credentials);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(Numeric.toHexString(message)).send();
            String transactionHash = ethSendTransaction.getTransactionHash();

            Message messageTransaction = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("HASH_TRANSACTION", transactionHash);
            messageTransaction.setData(bundle);
            handler.sendMessage(messageTransaction);

        } catch (IOException e) {
            Log.e(TAG, "Error sending money " + e.getMessage());
        }

    }


    public void stopThread() {
        interrupt();
    }
}
