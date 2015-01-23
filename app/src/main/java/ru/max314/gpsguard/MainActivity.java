package ru.max314.gpsguard;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.max314.util.threads.TimerUIHelper;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.tvDeviceTime)
    TextView tvDeviceTime;
    @InjectView(R.id.tvSpeed)
    TextView tvSpeed;
    @InjectView(R.id.tvCoord)
    TextView tvCoord;
    @InjectView(R.id.tvInfo)
    TextView tvInfo;
    @InjectView(R.id.tvInfoError)
    TextView tvInfoError;
    @InjectView(R.id.cbShowActivity)
    CheckBox cbShowActivity;
    @InjectView(R.id.cbCloseOnClick)
    CheckBox cbCloseOnClick;

    TimerUIHelper timerUIHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        cbShowActivity.setChecked(AppModel.getInstance().isShowActivityOnTrouble());
        cbCloseOnClick.setChecked(AppModel.getInstance().isCloseActivityOnClick());
        cbShowActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppModel.getInstance().setShowActivityOnTrouble(isChecked);
            }
        });
        cbCloseOnClick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppModel.getInstance().setCloseActivityOnClick(isChecked);
            }
        });


        timerUIHelper = new TimerUIHelper(1000, new Runnable() {
            @Override
            public void run() {
                updateData();
            }
        });
    }

    private void updateData(){
        AppModel model = AppModel.getInstance();
        Format format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String buff = format.format(date);
        tvDeviceTime.setText(buff);
        Location location = model.getCurrentLocation();
        if (location==null){
            tvSpeed.setText("0 м/с");
            tvCoord.setText("");
        }else {
            tvSpeed.setText(String.format("%3f м/с",location.getSpeed()));
            tvCoord.setText(String.format("%20.17f/%20.17f", location.getLatitude(), location.getLongitude()));
        }
        String pattern1="Месположение: <font color='blue'>%s</font> , фикс :<font color='blue'> %s</font> , проверочный фикс: <font color='blue'>%s</font> ";
        buff = String.format(pattern1, formatDate(model.getCurrentLocationDate()),formatDate(model.getCurrentLocationDateFix()),formatDate(model.getCheckLocationDateFix()));
        tvInfo.setText(Html.fromHtml(buff));
        String color = "green";
        String verifyString = model.getVerifyString();
        if (verifyString!=""){
            color = "red";
        }
        else{
            verifyString = "Ок";
        }

        buff = String.format("Проверка %s: <font color='%s'>%s</font>", formatDate(model.getVerifyDate()),color, verifyString);
        tvInfoError.setText(Html.fromHtml(buff));
    }

    private String formatDate(Date value){
        if (value == null) return "Нет";
        Format format = new SimpleDateFormat("HH:mm:ss");
        String buff = format.format(value);
        return buff;
    }


    //region OnClick
    @OnClick(R.id.btClearAGPS)
    public void onClearAGPSClick(){

    }
    @OnClick(R.id.btLoadAGPS)
    public void onLoadAGPSClick(){

    }

    @OnClick(R.id.btHide)
    public void onHideClick(){
        this.finish();

    }
    //endregion

    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
}
