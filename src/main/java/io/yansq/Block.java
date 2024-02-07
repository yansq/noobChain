package io.yansq;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author yansq
 * @version V1.0
 * @package io.yansq
 * @date 2024/2/6 13:55
 */
public class Block {
    // The hash is calculated by the previous block's hash.
    // If the previous block's hash is changed, this hash is also changed.
    @Getter
    private String hash;

    @Getter
    private String previousHash;

    private String merkleRoot;

    public List<Transaction> transactions = new ArrayList<>();

    private final Long timestamp;

    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(previousHash
                + timestamp + nonce + merkleRoot);
    }

    /**
     * Mine a block
     *
     * @param difficulty the number of 0's which must be solved for.
     */
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!: " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        if (!Objects.equals(previousHash, "0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block.");
        return true;
    }
}
