package com.centerprime.tronsdk.sdk;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.centerprime.tronsdk.api.GrpcAPI;
import com.centerprime.tronsdk.common.crypto.ECKey;
import com.centerprime.tronsdk.common.crypto.Hash;
import com.centerprime.tronsdk.common.crypto.Sha256Hash;
import com.centerprime.tronsdk.common.utils.Base58;
import com.centerprime.tronsdk.common.utils.ByteArray;
import com.centerprime.tronsdk.common.utils.TransactionUtils;
import com.centerprime.tronsdk.core.config.Parameter;
import com.centerprime.tronsdk.core.exception.CancelException;
import com.centerprime.tronsdk.core.exception.CipherException;
import com.centerprime.tronsdk.core.exception.EncodingException;
import com.centerprime.tronsdk.keystore.StringUtils;
import com.centerprime.tronsdk.keystore.WalletFile;
import com.centerprime.tronsdk.keystore.WalletUtils;
import com.centerprime.tronsdk.protos.Protocol;
import com.centerprime.tronsdk.walletcli.WalletApiWrapper;
import com.centerprime.tronsdk.walletserver.WalletApi;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CenterPrime on 2020/12/14.
 */
public class TronWalletManager {

    private static final TronWalletManager ourInstance = new TronWalletManager();

    public static TronWalletManager getInstance() {
        return ourInstance;
    }

    public TronWalletManager() {
    }


    private TronApi tronApi;



    public void init(Context context) {
        WalletApi.reloadClient(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apilist.tronscan.org/")
//                .baseUrl("https://api.shasta.trongrid.io/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        tronApi = retrofit.create(TronApi.class);
    }

    /**
     * Create Wallet by password
     */
    public Single<Wallet> createWallet(String password, Context context) {
        return Single.fromCallable(() -> {
            HashMap<String, Object> body = new HashMap<>();
            body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
            try {
                WalletApiWrapper walletApi = new WalletApiWrapper();
                walletApi.registerWallet(password.toCharArray(), context);
                String walletAddress = walletApi.getAddress();
                String keystore = exportKeyStore(walletAddress, context).blockingGet();
                body.put("action_type", "WALLET_CREATE");
                body.put("wallet_address", walletAddress);
                body.put("status", "SUCCESS");
                sendEventToLedger(body, context);
                return new Wallet(walletAddress, keystore);
            } catch (Exception e) {
                e.printStackTrace();
                body.put("status", "FAILURE");
            }
            sendEventToLedger(body, context);
            return null;
        });
    }

    /**
     * Import by Keystore
     */
    public Single<Wallet> importByKeystore(String password, String keystore, Context context) {
        return Single.fromCallable(() -> {
            HashMap<String, Object> body = new HashMap<>();
            body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
            try {
                WalletFile walletFile = WalletUtils.loadFromKeystore(keystore, context);
                WalletApi walletApi = new WalletApi(walletFile);
                byte[] passwd = StringUtils.char2Byte(password.toCharArray());

                boolean isSuccess = walletApi.checkPassword(passwd);
                body.put("action_type", "WALLET_IMPORT_KEYSTORE");
                body.put("wallet_address", walletFile.getAddress());
                body.put("status", "SUCCESS");
                sendEventToLedger(body, context);
                return new Wallet(walletFile.getAddress(), keystore);
            } catch (JsonSyntaxException | CipherException e) {
                e.printStackTrace();
                body.put("status", "FAILURE");
                sendEventToLedger(body, context);
                throw e;
            }
        });
    }

    /**
     * Import by Private Key
     */
    public Single<Wallet> importByPrivateKey(String privateKey, String password, Context context) {
        return Single.fromCallable(() -> {
            HashMap<String, Object> body = new HashMap<>();
            body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
            try {
                WalletApiWrapper walletApi = new WalletApiWrapper();
                walletApi.importWallet(password.toCharArray(), privateKey);

                String walletAddress = walletApi.getAddress();
                String keystore = exportKeyStore(walletAddress, context).blockingGet();
                //String keystore = getKeyStore(walletAddress, context).blockingGet();
                body.put("action_type", "WALLET_IMPORT_PRIVATE_KEY");
                body.put("wallet_address", walletAddress);
                body.put("status", "SUCCESS");
                sendEventToLedger(body, context);
                return new Wallet(walletAddress, keystore);
            } catch (JsonSyntaxException | CipherException e) {
                e.printStackTrace();
                body.put("status", "FAILURE");
                sendEventToLedger(body, context);
                throw e;
            }
        });
    }

    /**
     * Export Keystore by wallet address
     */
    public Single<String> exportKeyStore(String walletAddress, Context context) {
        return Single.fromCallable(() -> {

            String walletPath = context.getFilesDir().getPath() + "/" + "tron_" + walletAddress.toLowerCase() + ".json";
            File keystoreFile = new File(walletPath);
            if (keystoreFile.exists()) {
                return read_file(context, keystoreFile.getName());
            } else {
                throw new Exception("Keystore is NULL");
            }
        });
    }

    /**
     * Export private key by wallet address
     */
    public Single<String> exportPrivateKey(String walletAddress, String password, Context context) {
        return Single.fromCallable(() -> {
            HashMap<String, Object> body = new HashMap<>();
            body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
            try {
                WalletApiWrapper walletApi = new WalletApiWrapper();
                String walletPath = "tron_" + walletAddress.toLowerCase() + ".json";
                walletApi.loginAndroid(password.toCharArray(), walletPath, context);
                byte[] priKey = walletApi.backupWallet(password.toCharArray(), walletPath, context);

                String privatekey = (bytesToHex(priKey));
                body.put("action_type", "WALLET_EXPORT_PRIVATE_KEY");
                body.put("wallet_address", walletAddress);
                body.put("status", "SUCCESS");
                return privatekey;
            } catch (IOException | CipherException e) {
                e.printStackTrace();
                body.put("action_type", "WALLET_EXPORT_PRIVATE_KEY");
                body.put("wallet_address", walletAddress);
                body.put("status", "FAILURE");
                sendEventToLedger(body, context);
                throw e;
            }
        });
    }

    /**
     * Get Trx Balance of Wallet
     */
    public Single<BigDecimal> getBalanceTrx(String address, Context context) {
        return Single.fromCallable(() -> {
            try {

                HashMap<String, Object> trx20Balance = tronApi.getTrxBalance(address).blockingGet();
                if (trx20Balance != null) {
                    double amount = (double) trx20Balance.get("balance");
                    BigDecimal big = new BigDecimal(amount);
                    big = big.divide(new BigDecimal(1000000));
                    double balance = big.floatValue();

                    HashMap<String, Object> body = new HashMap<>();
                    body.put("action_type", "COIN_BALANCE");
                    body.put("wallet_address", address);
                    body.put("balance", balance);
                    body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
                    body.put("status", "SUCCESS");
                    sendEventToLedger(body, context);

                    return new BigDecimal(balance);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return null;

        });
    }

    /**
     *  Get transactionsHistory
     */
    public Single<HashMap<String, Object>> getTransactions(String sort, String count, int limit, int start, String address) {
        return Single.fromCallable(() -> {
            try {

                HashMap<String, Object> trxTransactions = tronApi.getTransactionAddress(sort, count, limit, start, address).blockingGet();
                return trxTransactions;

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return null;

        });
    }

    /**
     *  Get InfoTransaction
     */
    public Single<HashMap<String, Object>> getInfoTransaction(String hash) {
        return Single.fromCallable(() -> {
            try {

                HashMap<String, Object> trxTransactions = tronApi.getTransactionInfo(hash).blockingGet();
                return trxTransactions;

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return null;

        });
    }

    /**
     * Get TRX20 Token Balance of Wallet
     */
    public Single<BigDecimal> getTokenTRX20Balance(String walletAddress, String tokenContractAddress, Context context) {
        return Single.fromCallable(() -> {
            try {
                BigDecimal tokenBalance;
                WalletApiWrapper walletApi = new WalletApiWrapper();
                String transferMethod = "balanceOf(address)";
                String transferParams = "\"" + walletAddress + "\"";
                String asd = walletApi.triggerContract(walletAddress, tokenContractAddress, transferMethod, transferParams);
                System.out.println(asd);
                HashMap<String, Object> trx20Balance = tronApi.getTrx20Balance(walletAddress, tokenContractAddress).blockingGet();
                if (trx20Balance.containsKey("trc20token_balances")) {
                    List<LinkedTreeMap<String, Object>> mapList = (List<LinkedTreeMap<String, Object>>) trx20Balance.get("trc20token_balances");
                    BigDecimal balance = BigDecimal.ZERO;
                    for (LinkedTreeMap<String, Object> map : mapList) {
                        if (map.get("tokenId").equals(tokenContractAddress)) {
                            double amount = Double.parseDouble((String) map.get("balance"));
                            balance = BigDecimal.valueOf(amount);
                        }
                    }
                    tokenBalance = balance;
                } else {
                    tokenBalance = BigDecimal.ZERO;
                }

                HashMap<String, Object> body = new HashMap<>();
                body.put("action_type", "TOKEN_BALANCE");
                body.put("wallet_address", walletAddress);
                body.put("balance", tokenBalance.doubleValue());
                body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
                body.put("token_smart_contract", tokenContractAddress);
                body.put("token_name" , "tokenName");
                body.put("token_symbol" , "tokenSymbol");
                sendEventToLedger(body, context);

                return tokenBalance;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return null;

        });
    }

    /**
     * Send Trx
     */
    public Single<String> sendTRX(Context context, String sender, String password, String receiver, BigDecimal amount) {
        return Single.fromCallable(() -> {
            BigDecimal big2 = amount;
            big2 = big2.multiply(new BigDecimal(1000000));
            String toAddress = receiver;

            WalletApiWrapper walletApi = new WalletApiWrapper();
            String walletPath = "tron_" + sender.toLowerCase() + ".json";
            walletApi.loginAndroid(password.toCharArray(), walletPath, context);
            byte[] priKey = walletApi.backupWallet(password.toCharArray(), walletPath, context);

            String privatekey = (bytesToHex(priKey));

            long lAmount = big2.longValue();
            //toAddress = "TZ2XqAyRJ5UuKmN5DbFsnxGcu1ir1swhEK";
            GrpcAPI.EasyTransferResponse response = WalletApi
                    .easyTransferByPrivate(ByteArray.fromHexString(privatekey),
                            WalletApi.decodeFromBase58Check(toAddress), lAmount);

            if (response.getResult().getResult() == true) {
                Protocol.Transaction transaction = response.getTransaction();
                System.out.println("Easy transfer successful!!!");
                String txId = ByteArray.toHexString(response.getTxid().toByteArray());
                System.out.println("Receive txid = " + txId);

                HashMap<String, Object> body = new HashMap<>();
                body.put("action_type", "SEND_TRX");
                body.put("from_wallet_address", sender);
                body.put("to_wallet_address", receiver);
                body.put("amount", amount.doubleValue());
                body.put("tx_hash", txId);
                body.put("status", "SUCCESS");
                body.put("network", isMainNet() ? "MAINNET" : "TESTNET");
                sendEventToLedger(body, context);

                return txId;
            }
            return "";
        });
    }

    /**
     * Send Trc 20
     */
    public Single<String> sendTRX20(Context context, String sender, String password, String receiver, String contractAddress, BigDecimal amount) {
        return Single.fromCallable(() -> {
            BigDecimal big2 = amount;
            big2 = big2.multiply(new BigDecimal(1000000));
            String toAddress = receiver;

            WalletApiWrapper walletApi = new WalletApiWrapper();
            String walletPath = "tron_" + sender.toLowerCase() + ".json";
            walletApi.loginAndroid(password.toCharArray(), walletPath, context);
            byte[] priKey = walletApi.backupWallet(password.toCharArray(), walletPath, context);
            String privatekey = (bytesToHex(priKey));
            String transferParams = toAddress + "," + big2.stripTrailingZeros().toPlainString();

            String[] parameters = new String[]{contractAddress,
                    "transfer(address,uint256)", transferParams, "false", "100000000", "0"};
            GrpcAPI.TransactionExtention transactionExtention = null;
            try {
                transactionExtention = walletApi.triggerContract(parameters, decodeFromBase58Check(sender));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (EncodingException e) {
                e.printStackTrace();
            }
            if (transactionExtention.hasResult()) {
                Protocol.Transaction transactionTRX20 = transactionExtention.getTransaction();
                // 签名
                Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(transactionTRX20);
                byte[] privateBytes = ByteArray.fromHexString(privatekey);
                ECKey ecKey = ECKey.fromPrivate(privateBytes);
                mTransactionSigned = walletApi.signTransaction(mTransactionSigned, ecKey);
                // 公告
                boolean sent = WalletApi.broadcastTransaction(mTransactionSigned);
                if (!sent){
                    return null;
                }
                String txHash = ByteArray
                        .toHexString(Sha256Hash.hash(mTransactionSigned.getRawData().toByteArray()));

                HashMap<String, Object> body = new HashMap<>();
                body.put("action_type", "SEND_TOKEN");
                body.put("from_wallet_address", sender);
                body.put("to_wallet_address", receiver);
                body.put("amount", amount.toPlainString());
                body.put("tx_hash", txHash);
//                body.put("gasLimit", gasLimit.toString());
//                body.put("gasPrice", gasPrice.toString());
//                body.put("fee", gasLimit.multiply(gasPrice).toString());
                body.put("network" , isMainNet() ? "MAINNET" : "TESTNET");
                body.put("token_smart_contract", contractAddress);

                body.put("token_name" , "tokenName");
                body.put("token_symbol" , "tokenSymbol");

                body.put("status", "SUCCESS");
                sendEventToLedger(body, context);

                return txHash;
            }
            return null;
        });
    }

    /**
     * Broadcast Trc 20 transform
     */

    public Single<Boolean> broadcastTRC20Transform(String transactionData){
        return Single.fromCallable(() -> {
            try {
                Protocol.Transaction transaction = Protocol.Transaction.parseFrom(ByteArray.fromHexString(transactionData));
                boolean sent = WalletApi.broadcastTransaction(transaction);
                return sent;
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                return false;
            }
        });
    }

    /**
     * Sign Trc 20 transform
     */

    public Single<String> signTRX20Transform(Context context, String unsignedData, String signer, String password){
        return Single.fromCallable(() -> {
            WalletApiWrapper walletApi = new WalletApiWrapper();
            String walletPath = "tron_" + signer.toLowerCase() + ".json";
            walletApi.loginAndroid(password.toCharArray(), walletPath, context);
            byte[] priKey = walletApi.backupWallet(password.toCharArray(), walletPath, context);
            String privatekey = (bytesToHex(priKey));

            Protocol.Transaction unsignedTransaction = Protocol.Transaction.parseFrom(ByteArray.fromHexString(unsignedData));
            if (unsignedTransaction == null || unsignedTransaction.getRawData().getContractCount() == 0) {
                System.out.println("Invalid transaction !!!");
                return null;
            }
            Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(unsignedTransaction);
            byte[] privateBytes = ByteArray.fromHexString(privatekey);
            ECKey ecKey = ECKey.fromPrivate(privateBytes);
            mTransactionSigned = walletApi.signTransaction(mTransactionSigned, ecKey);
            if(null == mTransactionSigned){
                return null;
            }
            return ByteArray.toHexString(mTransactionSigned.toByteArray());
        });
    }

    /**
     * Create Trc 20 transform
     */
    public Single<String> createTRX20Transform(Context context, String sender, String password, String receiver, String contractAddress, BigDecimal amount) {
        return Single.fromCallable(() -> {
            BigDecimal big2 = amount;
            big2 = big2.multiply(new BigDecimal(1000000));
            String toAddress = receiver;

            WalletApiWrapper walletApi = new WalletApiWrapper();
            String walletPath = "tron_" + sender.toLowerCase() + ".json";
            walletApi.loginAndroid(password.toCharArray(), walletPath, context);
            byte[] priKey = walletApi.backupWallet(password.toCharArray(), walletPath, context);
            String privatekey = (bytesToHex(priKey));
            String transferParams = toAddress + "," + big2.stripTrailingZeros().toPlainString();

            String[] parameters = new String[]{contractAddress,
                    "transfer(address,uint256)", transferParams, "false", "100000000", "0"};
            GrpcAPI.TransactionExtention transactionExtention = null;
            try {
                transactionExtention = walletApi.triggerContract(parameters, decodeFromBase58Check(sender));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (EncodingException e) {
                e.printStackTrace();
            }
            if (transactionExtention.hasResult()) {
                Protocol.Transaction transactionTRX20 = transactionExtention.getTransaction();
                return ByteArray.toHexString(transactionTRX20.toByteArray());
            }
            return null;
        });
    }


    public static byte[] decode58Check(String input) {
        try {
            byte[] decodeCheck = Base58.decode(input);
            if (decodeCheck.length <= 4) {
                return null;
            }
            byte[] decodeData = new byte[decodeCheck.length - 4];
            System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
            byte[] hash0 = Hash.sha256(decodeData);
            byte[] hash1 = Hash.sha256(hash0);
            if (hash1[0] == decodeCheck[decodeData.length] &&
                    hash1[1] == decodeCheck[decodeData.length + 1] &&
                    hash1[2] == decodeCheck[decodeData.length + 2] &&
                    hash1[3] == decodeCheck[decodeData.length + 3]) {
                return decodeData;
            }
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decodeFromBase58Check(String addressBase58) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(addressBase58)) {
            return null;
        }
        byte[] address = decode58Check(addressBase58);
        if (!isAddressValid(address)) {
            return null;
        }
        return address;
    }

    public static boolean isAddressValid(byte[] address) {
        if (address == null || address.length == 0) {
            return false;
        }
        if (address.length != Parameter.CommonConstant.ADDRESS_SIZE) {
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != Parameter.CommonConstant.ADD_PRE_FIX_BYTE) {
            return false;
        }

        return true;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String read_file(Context context, String filename) throws IOException {
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private boolean isMainNet() {
        return true;
    }

    private void sendEventToLedger(HashMap<String, Object> map, Context context) {
        return;
        /*
        try {
            SubmitTransactionModel submitTransactionModel = new SubmitTransactionModel();
            submitTransactionModel.setTx_type("TRON");
            submitTransactionModel.setUsername("user1");
            submitTransactionModel.setOrgname("org1");

            HashMap<String, Object> deviceInfo = deviceInfo(context);
            if (deviceInfo != null) {
                map.put("DEVICE_INFO", new Gson().toJson(deviceInfo));
            }

            submitTransactionModel.setBody(map);
            hyperLedgerApi.submitTransaction(submitTransactionModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((objectBaseResponse, throwable) -> {
                        System.out.println(objectBaseResponse);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    private HashMap<String, Object> deviceInfo(Context context) {
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String osName = "ANDROID";
            String serialNumber = Build.SERIAL;
            String model = Build.MODEL;
            String manufacturer = Build.MANUFACTURER;
            HashMap<String, Object> deviceInfo = new HashMap<>();
            deviceInfo.put("ID", androidId);
            deviceInfo.put("OS", osName);
            deviceInfo.put("MODEL", model);
            deviceInfo.put("SERIAL", serialNumber);
            deviceInfo.put("MANUFACTURER", manufacturer);
            return deviceInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
