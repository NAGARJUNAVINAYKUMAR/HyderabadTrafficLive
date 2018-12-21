package com.tspolice.htplive.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tspolice.htplive.R;
import com.tspolice.htplive.adapters.AlertsAdapter;
import com.tspolice.htplive.adapters.CommonRecyclerAdapter;
import com.tspolice.htplive.adapters.MyRecyclerViewItemDecoration;
import com.tspolice.htplive.models.AlertsModel;
import com.tspolice.htplive.models.CommonModel;
import com.tspolice.htplive.network.URLs;
import com.tspolice.htplive.network.VolleySingleton;
import com.tspolice.htplive.utils.UiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private UiHelper mUiHelper;
    private RecyclerView mRecyclerView;
    private List<AlertsModel> mAlertsList;
    private AlertsAdapter mAlertsAdapter;
    private TextView tv_load_more;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        mRecyclerView = findViewById(R.id.mRecyclerViewAlerts);
        tv_load_more = findViewById(R.id.tv_load_more);

        mUiHelper = new UiHelper(AlertsActivity.this);
        mAlertsList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setSelected(true);
        mRecyclerView.addItemDecoration(new MyRecyclerViewItemDecoration(AlertsActivity.this,
                DividerItemDecoration.VERTICAL, 8));

        getPublicAdvisaryData();

        tv_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPubAdvNextRec(id);
            }
        });
    }

    private void getPublicAdvisaryData() {
        mUiHelper.showProgressDialog(getResources().getString(R.string.please_wait), false);
        VolleySingleton.getInstance(AlertsActivity.this).addToRequestQueue(new JsonArrayRequest(Request.Method.GET,
                URLs.getPublicAdvisaryData, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mUiHelper.dismissProgressDialog();
                        if (response != null && !"".equals(response.toString())
                                && !"null".equals(response.toString()) && response.length() > 0) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    AlertsModel model = new AlertsModel();
                                    model.setId(jsonObject.getString("id"));
                                    model.setAdvise(jsonObject.getString("advise"));
                                    model.setUpdatedDate(jsonObject.getString("updatedDate"));
                                    mAlertsList.add(model);
                                    if (i == 0) {
                                        id = mAlertsList.get(i).getId();
                                        Log.i("id-->", id);
                                    }
                                }
                                mAlertsAdapter = new AlertsAdapter(mAlertsList, AlertsActivity.this);
                                mRecyclerView.setAdapter(mAlertsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mUiHelper.showToastShort(getResources().getString(R.string.something_went_wrong));
                            }
                        } else {
                            mUiHelper.showToastShort(getResources().getString(R.string.empty_response));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mUiHelper.dismissProgressDialog();
                mUiHelper.showToastShort(getResources().getString(R.string.error));
            }
        }));
    }

    private void loadPubAdvNextRec(final String id) {
        mUiHelper.showProgressDialog(getResources().getString(R.string.please_wait), false);
        VolleySingleton.getInstance(AlertsActivity.this).addToRequestQueue(new JsonArrayRequest(Request.Method.GET,
                URLs.loadPubAdvNextRec(id), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mUiHelper.dismissProgressDialog();
                        if (response != null && !"".equals(response.toString())
                                && !"null".equals(response.toString()) && response.length() > 0) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    AlertsModel model = new AlertsModel();
                                    model.setId(jsonObject.getString("id"));
                                    model.setAdvise(jsonObject.getString("advise"));
                                    model.setUpdatedDate(jsonObject.getString("updatedDate"));
                                    mAlertsList.add(model);
                                }
                                mAlertsAdapter = new AlertsAdapter(mAlertsList, AlertsActivity.this);
                                mRecyclerView.setAdapter(mAlertsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mUiHelper.showToastShort(getResources().getString(R.string.something_went_wrong));
                            }
                        } else {
                            mUiHelper.showToastShort(getResources().getString(R.string.empty_response));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mUiHelper.dismissProgressDialog();
                mUiHelper.showToastShort(getResources().getString(R.string.error));
            }
        }));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
