package io.yansq;

import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yansq
 * @version V1.0
 * @package io.yansq
 * @date 2024/2/6 15:40
 */
@Getter
public class Transaction {
    // also the hash of transaction
    @Setter
    private String transactionId;

    private PublicKey sender;

    private PublicKey recipient;

    private float value;

    private byte[] signature;

    private List<TransactionInput> inputs;

    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    // a rough count of how many transactions have been generated
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calulateHash() {
        sequence++;
        return StringUtil.applySha256(StringUtil.getStringFromKey(sender)
                + StringUtil.getStringFromKey(recipient)
                + value + sequence
        );
    }

    /**
     * Sign all the data we don't wish to be tampered with.
     *
     * @param privateKey
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        this.signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * Verify the data we signed hasn't been tampered with.
     *
     * @return
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * If the new transaction could be created.
     *
     * @return boolean
     */
    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calulateHash();
        // send the value to recipient
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        // send the left over 'change' back to sender
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        for (TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO lists as spent
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue;
            }
            NoobChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    /**
     * The sum of inputs(UTXOs) values
     *
     * @return sum
     */
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue;
            }
            total += i.UTXO.value;
        }
        return total;
    }

    /**
     * The sum of outputs(UTXOs) values
     *
     * @return sum
     */
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
