package au.edu.sydney.comp5216.orphan.user.financial;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import au.edu.sydney.comp5216.orphan.R;
import au.edu.sydney.comp5216.orphan.user.financial.Config.Config;
import au.edu.sydney.comp5216.orphan.user.topTab.UserTab;

/**
 * This class is used for users to make payment to their financial donations.
 *
 * We write Google Pay methods here but we do not implement them because of some reasons.
 *
 * @author Siqi Wu
 */
public class PaymentDonate extends AppCompatActivity {
    //String data of received intent extra field representing the current selected orphanage ID and the orphanage's name
    private String currentOrphanId;
    private String currentOrphanName;

    public static final int PAYPAL_REQUEST_CODE = 7171;

    //initialize PayPal
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String amount = "";

    //widgets in the page
    private Button dollar10;
    private Button dollar30;
    private Button dollar50;
    private Button dollar100;
    private Button donateNowBtn;
    private Button cancelDonateBtn;
    private TextView moneyLeft;
    private EditText moneySpecific;
    private EditText leaveMessage;

    //Define the firebase firestore
    private FirebaseFirestore db;

    //Define the money donate
    private int moneyDonate;

    //Define the Map data, which is used to store the data to be uploaded
    private Map<String, Object> userFinancialDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_donate);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        //Initialize the widgets
        dollar10 = findViewById(R.id.dollar10);
        dollar30 = findViewById(R.id.dollar30);
        dollar50 = findViewById(R.id.dollar50);
        dollar100 = findViewById(R.id.dollar100);
        donateNowBtn = findViewById(R.id.donateNowBtn);
        cancelDonateBtn = findViewById(R.id.cancelDonateBtn);
        moneySpecific = findViewById(R.id.money_specific);
        leaveMessage = findViewById(R.id.leaveMessage);
        moneyLeft = findViewById(R.id.money_left);

        //receive the data from the pass intent string extra field
        currentOrphanId = getIntent().getStringExtra("currentOrphanId");
        currentOrphanName = getIntent().getStringExtra("currentOrphanName");

        //read the current latest donation information from the firestore and set the textview in the current page
        readDataAndSetMoneyLeft();
    }

    /**
     * Once the $10 button is clicked, set the textview of moneySpecific to be 10
     *
     * @param v the current view of the page
     */
    public void onClickDollar10(View v) {
        moneySpecific.setText("" + 10);
    }

    /**
     * Once the $30 button is clicked, set the textview of moneySpecific to be 30
     *
     * @param v the current view of the page
     */
    public void onClickDollar30(View v) {
        moneySpecific.setText("" + 30);
    }

    /**
     * Once the $50 button is clicked, set the textview of moneySpecific to be 50
     *
     * @param v the current view of the page
     */
    public void onClickDollar50(View v) {
        moneySpecific.setText("" + 50);
    }

    /**
     * Once the $100 button is clicked, set the textview of moneySpecific to be 100
     *
     * @param v the current view of the page
     */
    public void onClickDollar100(View v) {
        moneySpecific.setText("" + 100);
    }

    /**
     * Read the current latest financial donation information of the selected orphanage
     */
    public void readDataAndSetMoneyLeft() {
        db = FirebaseFirestore.getInstance();

        /**
         * If successfully read the data, then set the textview with the specific number read from the firestore
         * */
        db.collection("orphanFinancialDonation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String orphanId = document.get("id").toString();
                        if (orphanId.equals(currentOrphanId)) {
                            int goal = Integer.parseInt(document.get("goal").toString());
                            int currentNumber = Integer.parseInt(document.get("currentNumber").toString());
                            int left = goal - currentNumber;
                            moneyLeft.setText("$" + (goal - currentNumber) + " left");
                        }
                    }
                } else {
                }
            }
        });
    }

    /**
     * If the donate now button is clicked, then upload the current user's donation data(call the uploadSuccessDonate ()
     * button.
     *
     * @param v the current view of this page
     */
    public void onClickDonateNow(View v) throws JSONException {
        processPayment();
//        uploadSuccessfulDonate();
    }

    /**
     * Use PayPal sdk process payment
     */
    private void processPayment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentDonate.this);
        builder.setTitle("Some inputs are missing!");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        userFinancialDonation = new HashMap<>();

        //receive the shared data
        SharedPreferences gamesettings = this.getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        String userId = gamesettings.getString("userId", null);
        userFinancialDonation.put("userId", userId);
        String userName = gamesettings.getString("userName", "Anonymous");
        userFinancialDonation.put("donateUserName", userName);

        userFinancialDonation.put("orphanId", currentOrphanId);
        userFinancialDonation.put("currentOrphanName", currentOrphanName);


        //check if there is any blank in the input box
        if (moneySpecific.getText() != null && moneySpecific.getText().length() != 0) {
            userFinancialDonation.put("donateNumber", moneySpecific.getText().toString());
            moneyDonate = Integer.parseInt(moneySpecific.getText().toString());
        } else {
            alert.show();
        }
        if (leaveMessage.getText() != null && leaveMessage.getText().length() != 0) {
            userFinancialDonation.put("leaveMessage", leaveMessage.getText().toString());
        } else {
            alert.show();
        }

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        userFinancialDonation.put("donateTime", timeStamp);

        if (userFinancialDonation.size() == 7) {
            amount = moneySpecific.getText().toString();
            PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "AUD",
                    "Donate", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PayPalService.ACCOUNT_SERVICE, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }


    }

    /**
     * After payment successful, save transaction data to service
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null ){
                    uploadSuccessfulDonate();

                }
            }else if(resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stop PayPal service
     */
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    /**
     * If the Cancel Click button is clicked, then return to the previous activity with the current orphanage ID
     *
     * @param v the current view of this page
     */
    public void onClickCancelDonate(View v) {
        Intent returnIntent = new Intent(PaymentDonate.this, UserTab.class);
        returnIntent.putExtra("orphanId", currentOrphanId);
        startActivity(returnIntent);
    }

    /**
     * Upload the user's donate information to the firestore. Before uploading, check if there is any blank in the input
     * box. If yes, then prompt users with dialog messages. If not, upload the information to the firestore.
     */
    public void uploadSuccessfulDonate() {
        final FirebaseFirestore db;
        // create connection to Firestore
        db = FirebaseFirestore.getInstance();
        //check if there is any blank in the input box

        if (userFinancialDonation.size() == 7) {
            db.collection("userFinancialDonate").add(userFinancialDonation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    updateDonorsAndMoney(db, (String) userFinancialDonation.get("donateTime"));
                    Toast.makeText(PaymentDonate.this, "Donated Successfully!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaymentDonate.this, "Failed to donate.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Once the user has successfully donate, the information stored in the current orphanage's donation should also be
     * updated. Update the donors number, the current donated money.
     * After successfully upload the donation information, go back to the information page with updated data
     *
     * @param db FirebaseFirestore instance
     * @param timeStamp the time that user makes successful donation
     */
    public void updateDonorsAndMoney(final FirebaseFirestore db, final String timeStamp) {
        db.collection("orphanFinancialDonation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String orphanId = document.get("id").toString();
                        if (orphanId.equals(currentOrphanId)) {
                            String id = document.getId();
                            int donorsNumber = Integer.parseInt(document.get("donorsNumber").toString());
                            int currentNumber = Integer.parseInt(document.get("currentNumber").toString());
                            Map<String, Object> updateValues = new HashMap<>();
                            updateValues.put("donorsNumber", (++donorsNumber) + "");
                            updateValues.put("currentNumber", (currentNumber + moneyDonate));
                            db.collection("orphanFinancialDonation").document(id).update(updateValues);

                            try {
                                saveToLocalCache(timeStamp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                        }

                        Intent returnIntent = new Intent(PaymentDonate.this, UserTab.class);
                        returnIntent.putExtra("orphanId", currentOrphanId);
                        startActivity(returnIntent);
                    }
                } else {
                }
            }
        });
    }

    /**
     * Writes current users' donation to local application cache folders in a customized form to save the storage Once
     * the application is installed, the data will be cleared automatically. And here the user data will only be used
     * for our app Other app will not have access to this data.
     *
     * @param timeStamp the time that the user make successful donation
     */
    private void saveToLocalCache(String timeStamp) throws IOException {
        // create file
        SharedPreferences gamesettings = this.getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        String userName = gamesettings.getString("userName", "Anonymous");

        File cacheFile = new File(this.getCacheDir(), userName + ".usr");
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // write to the file
        FileOutputStream fos = new FileOutputStream(cacheFile, true);
        StringBuilder sb = new StringBuilder();
        sb.append(userName);
        sb.append(":" + currentOrphanId);
        sb.append(":" + currentOrphanName);
        sb.append(":" + moneyDonate);
        sb.append(":" + timeStamp);
        sb.append("\n");
        fos.write(sb.toString().getBytes());
        fos.close();
    }
}