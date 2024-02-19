package com.example.blockchain;

import java.util.ArrayList;
import java.util.Date;

class Block {
    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }
}

class Blockchain {
    private ArrayList<Block> blockchain = new ArrayList<>();
    private int difficulty = 5;

    public void addBlock(Block newBlock) {
        blockchain.add(newBlock);
    }

    public void mineBlock(Block block) {
        block.mineBlock(difficulty);
        System.out.println("Blocul " + block.getData() + " a fost minat!");
    }

    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Hash-urile curente nu sunt egale");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Hash-urile anterioare nu sunt egale");
                return false;
            }
        }
        return true;
    }

    public ArrayList<Block> getBlockchain() {
        return blockchain;
    }
}

class StringUtil {
    public static String applySha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

public class TestChain {
    public static void main(String[] args) {
        Blockchain chain = new Blockchain();

        System.out.println("Încerc să minez blocul 1...");
        Block block1 = new Block("Test 1", "0");
        chain.addBlock(block1);
        chain.mineBlock(block1);

        System.out.println("Încerc să minez blocul 2...");
        Block block2 = new Block("Test 2", chain.getLatestBlock().hash);
        chain.addBlock(block2);
        chain.mineBlock(block2);

        System.out.println("Încerc să minez blocul 3...");
        Block block3 = new Block("Test 3", chain.getLatestBlock().hash);
        chain.addBlock(block3);
        chain.mineBlock(block3);

        System.out.println("\nBlockchain-ul este valid: " + chain.isChainValid());

        System.out.println("\nBlockchain-ul:");
        for (Block block : chain.getBlockchain()) {
            System.out.println("{");
            System.out.println("  \"hash\": " + block.hash + ",");
            System.out.println("  \"previousHash\": " + block.previousHash + ",");
            System.out.println("  \"data\": " + block.getData() + ",");
            System.out.println("  \"timeStamp\": " + new Date(block.getTimeStamp()) + ",");
            System.out.println("  \"nonce\": " + block.getNonce());
            System.out.println("}");
        }
    }
}
