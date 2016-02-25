package com.shomen.MUChat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.shomen.MUChat.Singletons.VolleyController;
import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.Models.Results;
import com.shomen.MUChat.Models.ServerResponse;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Utils.CONSTANT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by server on 1/19/2016.
 */
public class SignUpFragment extends Fragment{

    private String LOG_TAG = "ASL_"+this.getClass().getSimpleName() ;
    private Gson gson = new Gson();
    private ProgressDialog progressDialog;

    @Bind(R.id.input_name) EditText etNameText;
    @Bind(R.id.input_email) EditText etEmailText;
    @Bind(R.id.input_password) EditText etPasswordText;
    @Bind(R.id.btn_signup) Button btnSignUpButton;
    @Bind(R.id.link_login) TextView tvLoginLink;

    public SignUpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate method");
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onCreateView method");
        getActivity().invalidateOptionsMenu();

        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, rootView);

        inItPDialouge();
        btnSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    public void signup() {
        Log.d(LOG_TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }
        requestToServer();

        btnSignUpButton.setEnabled(false);

        showPDialouge();

/*        String name = etNameText.getText().toString();
        String email = etEmailText.getText().toString();
        String password = etPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        hidePDialouge();
                    }
                }, 3000);*/
    }

    private void inItPDialouge(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
    }

    private void showPDialouge(){
        progressDialog.show();
    }

    private void hidePDialouge(){
        progressDialog.hide();
    }

    public void onSignupSuccess() {
        hidePDialouge();
        btnSignUpButton.setEnabled(true);
        clearBackStack();
        getFragmentManager().beginTransaction().replace(R.id.main_act_fragment_container,new SongListFragment(),"SongListFragment").commit();

    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onSignupFailed() {
        hidePDialouge();
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnSignUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = etNameText.getText().toString();
        String email = etEmailText.getText().toString();
        String password = etPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            etNameText.setError("at least 3 characters");
            valid = false;
        } else {
            etNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            etEmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPasswordText.setError(null);
        }

        return valid;
    }

    private JSONObject makeRequestBody(){
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("username", etNameText.getText().toString().trim());
            jsonBodyObj.put("email", etEmailText.getText().toString().trim());
            jsonBodyObj.put("password", etPasswordText.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonBodyObj;
    }

    private void requestToServer(){

        final String requestBody = makeRequestBody().toString();

//        showProgressDialog();
        JsonObjectRequest jsonObjPostReq = new JsonObjectRequest(Request.Method.POST, CONSTANT.PRODUCTION_URL.SIGNUP_URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());

                        ///add exception handling//////////
                        ServerResponse res = gson.fromJson(response.toString(),ServerResponse.class);
                        Log.e(LOG_TAG, "---------" + res.toString());
                        for (Results d:res.getResults() ) {
                            Log.e(LOG_TAG,"---------"+d.toString());
                        }
                        if(res.getStatus() != null && res.getStatus().equals("success") && !res.getResults().isEmpty()){
                            LoginSession loginSession = new LoginSession(getActivity());
                            loginSession.createLoginSession(res.getResults().get(0).getEmail(),res.getResults().get(0).getName(),
                                    res.getResults().get(0).getId(),res.getResults().get(0).getToken());

                            onSignupSuccess();
                        }else{
                            Log.d(LOG_TAG,"Not authorized");
                            onSignupFailed();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "Error: " + error.getMessage());

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", "Androidhive");
                params.put("email", "abc@androidhive.info");
                params.put("pass", "password123");

                return params;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }

        };

        VolleyController.getInstance(getContext()).addToRequestQueue(jsonObjPostReq,"tag");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "inside onAttach method");
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "inside onDetach method");
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "inside onPause method");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "inside onResume method");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "inside onStop method");
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "inside onDestoy method");
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.d(LOG_TAG, "inside onCreateOptionsMenu method");

        menu.findItem(R.id.action_login).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "inside onPrepareOptionsMenu method");
        super.onPrepareOptionsMenu(menu);
    }
}
