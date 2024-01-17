package ro.pub.cs.systems.eim.votingapp;

import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

public class GanacheGasProvider implements ContractGasProvider {

    private final BigInteger gasPrice;
    private final BigInteger gasLimit;

    public GanacheGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return gasPrice;
    }

    @Override
    public BigInteger getGasPrice() {
        return gasPrice;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        return gasLimit;
    }

    @Override
    public BigInteger getGasLimit() {
        return gasLimit;
    }
}
