package au.edu.sydney.comp5216.orphan.user.financial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import au.edu.sydney.comp5216.orphan.R;

public class PaymentDetails extends AppCompatActivity {
    TextView textId, txtAmount, txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        textId = findViewById(R.id.textId);
        txtAmount = findViewById(R.id.textAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();


        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            textId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText(response.getString(String.format("$%s", paymentAmount)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

