package io.yansq;

import java.security.PublicKey;

/**
 * @author yansq
 * @version V1.0
 * @package io.yansq
 * @date 2024/2/6 15:42
 */
public class TransactionOutput {
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey == recipient;
    }
}
