package com.example.abdullahshah.chatroughdraftnew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFirstname;
    private EditText mLastname;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirstname = findViewById(R.id.fusername_ET);
        mLastname = findViewById(R.id.Lusername_ET);
        mUsername = findViewById(R.id.username_ET);
        mPassword = findViewById(R.id.password_ET);
        mEmail = findViewById(R.id.email_ET);
        mCreateBtn = findViewById(R.id.signupBtn);


        mProgressDialog = new ProgressDialog(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();

            }
        });

        findViewById(R.id.cardview_registeractivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });
    }

    private void registerUser(String username, String password, String email, String firstname) {
        final String upload_username = username;
        final String upload_email = email;
        final String upload_firstname = firstname;
        final String upload_lastname = mLastname.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    writeNewUser(uid, upload_username,  upload_email, "Hi There!",
                            "default", "default", upload_firstname, upload_lastname, 0, 0);
                    mProgressDialog.dismiss();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void writeNewUser(String userId, String username, String email, String status, String image,
                              String thumb_image, String firstname, String lastname, int followers, int following){
        User user = new User(username, email, status, image, thumb_image, firstname, lastname, followers, following);
        Map<String, Object> userValues = user.toMap();
        mDatabase.child("Users").child(userId).setValue(userValues);
        mDatabase.child("Users").child(userId).child("community_chosen").setValue("Meme-Feed");
    }

    private void attemptRegistration() {

        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.

        String email = mEmail.getText().toString();
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String firstname = mFirstname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid first name
        if (TextUtils.isEmpty(firstname)) {
            mFirstname.setError(getString(R.string.error_field_required));
            focusView = mFirstname;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mProgressDialog.setTitle("Registering User");
            mProgressDialog.setMessage("Process may be slow depending on your connection");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            registerUser(username, password, email, firstname);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, StartActivity.class));
        finish();
    }
}
