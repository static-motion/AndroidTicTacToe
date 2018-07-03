package com.example.tictactoe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.interfaces.ChangePrefDialogListener;
import com.example.tictactoe.utils.SharedPreferencesManager;
import com.example.tictactoe.views.EditPrefDialog;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, ChangePrefDialogListener{
    EditPrefDialog dialog = new EditPrefDialog();
    SharedPreferencesManager mManager;
    private TextView mNickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        findViewById(R.id.btn_change_nickname).setOnClickListener(this);
        mManager = SharedPreferencesManager.getInstance();
        dialog.setListener(this);
        mNickname = findViewById(R.id.nickname_settings);
        displayNickname();
    }

    private void displayNickname() {
        mNickname.setText(mManager.getPreference(SharedPreferencesManager.NICKNAME));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_nickname:
                dialog.setLengthLimit(3, 10).setPreference(SharedPreferencesManager.NICKNAME);
                break;
        }

        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void publishPreference(String mode, String preference) {
        mManager.savePreference(mode, preference);
        Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
        displayNickname();
    }
}