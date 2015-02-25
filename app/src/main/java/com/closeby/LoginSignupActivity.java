package com.closeby;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sam on 5/27/2014.
 */
public class LoginSignupActivity extends Activity{
    LinearLayout holderLayout;
    LinearLayout openLogin, openSignup, facebookLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_login_layout);


        getActionBar().hide();
        printKeyHash(this);

        openLogin = (LinearLayout) findViewById(R.id.open_login_button);
        // openSignup = (LinearLayout) findViewById(R.id.open_signup_button);

        holderLayout = (LinearLayout) findViewById(R.id.login_button_holder_layout);

        final FragmentTransaction trans = getFragmentManager().beginTransaction();

        /*openSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holderLayout.setVisibility(View.GONE);
                trans.add(R.id.signup_login_layout, new SignupFragment());
                trans.commit();
            }
        });
*/
        openLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holderLayout.setVisibility(View.GONE);
                trans.add(R.id.signup_login_layout, new LoginFragment());
                trans.commit();
            }
        });

        facebookLogin = (LinearLayout) findViewById(R.id.login_with_facebook_button);
        final List<String> permissions = Arrays.asList("email","user_status");

        facebookLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(LoginSignupActivity.this, "", "Please Wait ...", true);
                ringProgressDialog.setCancelable(false);
                ParseFacebookUtils.logIn(permissions, LoginSignupActivity.this, Constants.FragmentConstants.LoginConstants.FacebookActivityCode ,new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if(user != null) {
                           ringProgressDialog.dismiss();
                           OpenHome();
                        }
                    }
                });
            }
        });
    }

    private void OpenHome() {
        Intent HomeActivity = new Intent(LoginSignupActivity.this, CreateProfileFragment.class);
        startActivity(HomeActivity);

        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
        OpenHome();

    /*
            ParseFacebookUtils.logIn(this, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        Toast.makeText(LoginSignupActivity.this, "Some Error Occoured", Toast.LENGTH_SHORT).show();
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                        Intent in = new Intent(LoginSignupActivity.this, MainActivity.class);
                        startActivity(in);
                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");
                        Intent in = new Intent(LoginSignupActivity.this, MainActivity.class);
                        startActivity(in);
                    }
                }
    */
//        });
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {

            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);

            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }

        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

}


class LoginFragment extends Fragment {
    EditText username, password;
    Button login_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_layout, container,false);
        UIinit(v);
        return v;
    }

    private void UIinit(View v) {
        username = (EditText) v.findViewById(R.id.login_username_email);
        password = (EditText) v.findViewById(R.id.login_password);

        login_button = (Button) v.findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_val = username.getText().toString();
                String password_val = password.getText().toString();

                ParseUser.logInInBackground(username_val, password_val, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        Toast.makeText(getActivity(), "Logging In User", Toast.LENGTH_SHORT).show();
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Toast.makeText(getActivity(), "USER LOGGED IN !", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getActivity(), MainActivity.class);
                            startActivity(in);
                        } else {
                            Toast.makeText(getActivity(), "Error Logging IN", Toast.LENGTH_SHORT).show();
                            // Signup failed. Look at the ParseException to see what happened.
                        }
                    }
                });
            }
        });
    }
}

class SignupFragment extends Fragment {
    EditText email,password,username;
    Button signup_button;
    ParseGeoPoint py;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup_layout, container,false);
        UIinit(v);

        return v;
    }

    private void UIinit(View v) {
        username = (EditText) v.findViewById(R.id.signup_username);
        password = (EditText) v.findViewById(R.id.signup_password);
        email = (EditText) v.findViewById(R.id.signup_email);

        signup_button = (Button) v.findViewById(R.id.signup_button);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_val = username.getText().toString();
                String email_val = email.getText().toString();
                String password_val = password.getText().toString();

                ParseUser user = new ParseUser();
                user.setUsername(username_val);
                user.setPassword(password_val);
                user.setEmail(email_val);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "User Signed Up", Toast.LENGTH_SHORT).show();
                            Intent Home = new Intent(getActivity(), MainActivity.class);
                            startActivity(Home);
                        } else {
                            Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                        }
                    }
                });
            }
        });
    }
}