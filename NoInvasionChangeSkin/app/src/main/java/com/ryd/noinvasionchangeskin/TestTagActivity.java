package com.ryd.noinvasionchangeskin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ryd.changskin.SkinManager;


public class TestTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tag);

        SkinManager.getInstance().register(this);
    }

    public void addNewView(View view) {
        //建议通过xml inflater
        TextView tv = new TextView(this);
        tv.setTag("skin:item_text_color:textColor");
        tv.setTextColor(getResources().getColorStateList(R.color.item_text_color));
        tv.setText("dymaic add!");

        ((ViewGroup) findViewById(R.id.id_container)).addView(tv);
        SkinManager.getInstance().injectSkin(tv);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
