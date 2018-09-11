package com.yz.dzq.ailuoku6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.ProgressStyle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class cjexcel extends AppCompatActivity {

    private LinearLayout mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjexcel);
        Intent intent = getIntent();
        String result = intent.getStringExtra("data");
        String name = getName(result);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(name);
        setSupportActionBar(mToolbar);

        mContentView = (LinearLayout) findViewById(R.id.main_table);
        initDisplayOpinion();
        ArrayList<ArrayList<String>> mTableDatas = new ArrayList<ArrayList<String>>();

        mTableDatas = getTable(result);

        final LockTableView mLockTableView = new LockTableView(this, mContentView, mTableDatas);

        mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(70) //列最大宽度
                .setMinColumnWidth(20) //列最小宽度
                .setMinRowHeight(20)//行最小高度
                .setMaxRowHeight(35)//行最大高度
                .setTextViewSize(12) //单元格字体大小
                .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                .setCellPadding(8)//设置单元格内边距(dp)
                .setNullableString("") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })//设置横向滚动回调监听
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
                        Log.e("滚动边界","滚动到最右边");
                    }
                })//设置横向滚动边界监听
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                        Log.e("点击事件",position+"");
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("长按事件",position+"");
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(false);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);
    }

    private String getName(String result){
        Document doc = Jsoup.parse(result);
        Elements trs = doc.select("table").select("tr");
        String xingming = trs.get(0).select("td").get(0).text();
        xingming=xingming.substring(0,xingming.length()-10);
        return xingming;
    }

    private ArrayList<ArrayList<String>> getTable(String result){
        ArrayList<ArrayList<String>> mTableDatas = new ArrayList<ArrayList<String>>();

        Document doc = Jsoup.parse(result);
        Elements trs = doc.select("table").select("tr");

        for (int i = 1;i<trs.size();i++){
            Elements tds = trs.get(i).select("td");
            ArrayList<String> mRowDatas = new ArrayList<String>();
            for (int j = 0;j<tds.size();j++){
                mRowDatas.add(tds.get(j).text());
            }
            mTableDatas.add(mRowDatas);
        }
        return mTableDatas;
    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

}
