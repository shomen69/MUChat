package com.shomen.MUChat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.shomen.MUChat.Models.Results;
import com.shomen.MUChat.Models.ServerResponse;
import com.shomen.MUChat.Singletons.VolleyController;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by server on 1/19/2016.
 */
public class LoginFragment extends Fragment {

    private String LOG_TAG = "ASL_"+this.getClass().getSimpleName() ;

    private Gson gson = new Gson();
    private  ProgressDialog pDialog;

    @Bind(R.id.input_email)
    EditText etEmailText;
    @Bind(R.id.input_password)
    EditText etPasswordText;
    @Bind(R.id.btn_login)
    Button btnLoginButton;
    @Bind(R.id.link_signup)
    TextView tvSignUpLink;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate method");
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "inside onStart method");
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onCreateView method");

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        inItPDialouge();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onActivityCreated method");
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.link_signup)
    public void startSignUpFragment(){
        SignUpFragment fragment = new SignUpFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_act_fragment_container, fragment, "SignUpFragment");
        ft.addToBackStack("SignUpFragment");
        ft.commit();
    }

    @OnClick(R.id.btn_login)
    public void login() {
        Log.d(LOG_TAG, "Login");

        btnLoginButton.setEnabled(false);

        if (!validate()) {
            onLoginFailed();
            return;
        }

        requestToServer();

/*        String email = etEmailText.getText().toString().trim();
        String password = etPasswordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        onLoginSuccess();
                        // onLoginFailed();
                        hidePDialouge();
                    }
                }, 3000);*/
    }

    private void inItPDialouge(){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Authenticating...");
        pDialog.setCancelable(false);
    }

    private void showPDialouge(){
        pDialog.show();
    }

    private void hidePDialouge(){
        pDialog.hide();
    }

    public void onLoginSuccess() {
        hidePDialouge();
        btnLoginButton.setEnabled(true);
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

    public void onLoginFailed() {
        hidePDialouge();
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnLoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = etEmailText.getText().toString().trim();
        String password = etPasswordText.getText().toString();

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
            jsonBodyObj.put("email", etEmailText.getText().toString().trim());
            jsonBodyObj.put("password", etPasswordText.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonBodyObj;
    }

    private void requestToServer(){

        showPDialouge();
        final String requestBody = makeRequestBody().toString();

        JsonObjectRequest jsonObjPostReq = new JsonObjectRequest(Request.Method.POST, CONSTANT.PRODUCTION_URL.LOGIN_URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        ServerResponse res = gson.fromJson(response.toString(),ServerResponse.class);
                        Log.d(LOG_TAG, "---------" + res.toString());

                        for (Results d:res.getResults() ) {
                            Log.d(LOG_TAG,"---------"+d.toString());
                        }

                        if(res.getStatus() != null && res.getStatus().equals("success") && !res.getResults().isEmpty()){
                            LoginSession loginSession = new LoginSession(getActivity());
                            loginSession.createLoginSession(res.getResults().get(0).getEmail(),res.getResults().get(0).getName(),
                                                            res.getResults().get(0).getId(),res.getResults().get(0).getToken());
                            onLoginSuccess();
                        }else{
                            Log.d(LOG_TAG,"not authorized...");
                            onLoginFailed();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "Error: " + error.getMessage());
                        onLoginFailed();

                    }
                }
        )
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
                params.put("name", "Shomen");
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

        VolleyController.getInstance(getContext()).addToRequestQueue(jsonObjPostReq, "tag");
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
