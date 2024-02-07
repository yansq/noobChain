package io.yansq;

/**
 * @author yansq
 * @version V1.0
 * @package io.yansq
 * @date 2024/2/6 15:42
 */
public class TransactionInput {
    // reference to TransactionOutputs -> transactionId
    public String transactionOutputId;

    // contains the unspent transaction output
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
