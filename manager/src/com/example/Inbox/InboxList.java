package com.example.Inbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;

import com.example.anand_roadwayss.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxList extends ActionBarActivity {
    DBAdapter db;
    Toolbar toolbar;
    LinearLayout noDataLayout;
    long val;
    ArrayList<Long> array;
    ImageView select;
    int count,pos;
    MyCursorAdapter caPersonal;

    CheckBox checkAll;
    CheckBox checkBox;
    ArrayList<Integer> mCheckedItems = new ArrayList();
    public View view1;
    ListView listPersonalAdvance;
    TextView delete,cancle;
    LinearLayout lenearCheck;
    Cursor inboxAccounts;
    TextView ok,cancel,message;
    CustomAlertDialog ald;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ald=new CustomAlertDialog();
        toolbar.setNavigationIcon(R.drawable.inboxopen);
        toolbar.setTitle("INBOX");
        setSupportActionBar(toolbar);
        db = new DBAdapter(this);
        noDataLayout = (LinearLayout) findViewById(R.id.inboxLinearL);
        select=(ImageView)findViewById(R.id.select);
        delete=(TextView)findViewById(R.id.delete);
        cancle=(TextView)findViewById(R.id.cancle);
        lenearCheck=(LinearLayout)findViewById(R.id.lenearCheck);
        checkAll = (CheckBox)findViewById(R.id.checkall);


    }

    @Override
    protected void onStart() {

        try {
            SharedPreferences UserType = getSharedPreferences(
                    "RegisterName", 0);
            String UserTyp = UserType.getString("Name", "");

            if (UserTyp.equals("DEMO")) {
                try {

                    String[] columnNames = {"_id", "Date", "Subject"};
                    MatrixCursor tripCursor = new MatrixCursor(columnNames);

                    tripCursor.addRow(new Object[]{1, "27-Aug-2015", "Vehicle Message"});
                    tripCursor.addRow(new Object[]{2, "26-Aug-2015", "Fuel pilferage alert"});
                    tripCursor.addRow(new Object[]{3, "27-Aug-2015", "Vehicle Message"});
                    tripCursor.addRow(new Object[]{2, "26-Aug-2015", "Fuel pilferage alert"});


                    String from[] = {"Date", "Subject"};
                    int to[] = {R.id.textViewinboxaccount1, R.id.textViewinboxaccount2};
                    caPersonal = new MyCursorAdapter(this, R.layout.account2, tripCursor, from, to, 0);
                    listPersonalAdvance = (ListView) findViewById(R.id.inboxListLV);
                    count = caPersonal.getCount();
                    listPersonalAdvance.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listPersonalAdvance.setAdapter(caPersonal);

                    SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                            listPersonalAdvance,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override

                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    try {
                                        for (int position : reverseSortedPositions) {
                                            val = array.get(position);
                                            db.open();
                                            db.deleteInboxItem(val);
                                            db.close();
                                            recreate();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                                + " " + "[ SwipeDismissListViewTouchListener]", e.toString());

                                    }
                                    caPersonal.notifyDataSetChanged();
                                }
                            });

                    listPersonalAdvance.setLongClickable(true);

                    listPersonalAdvance
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {

                                    String subjectType = ((TextView) view.findViewById(R.id.textViewinboxaccount2)).getText().toString();
                                    if (subjectType.equals("Fuel pilferage alert")) {
                                        Intent intent = new Intent(InboxList.this,
                                                InboxDetails.class);
                                        intent.putExtra("inboxId", "1");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(InboxList.this,
                                                InboxCommonDetails.class);
                                        intent.putExtra("inboxId", "1");
                                        startActivity(intent);
                                    }


                                }
                            });



                } catch (Exception e)
                {
                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                            + " " + "[ demo]", e.toString());
                }
            } else {
                db.open();
                inboxAccounts = db.retrieveInboxData();

                array = new ArrayList<>();
                while (inboxAccounts.moveToNext()) {
                    long id = inboxAccounts.getLong(inboxAccounts.getColumnIndex("_id"));
                    array.add(id);
                }
                String from[] = {DBAdapter.getKeySubject(), DBAdapter.getKeyDate()};
                int to[] = {R.id.textViewinboxaccount1, R.id.textViewinboxaccount2};
                caPersonal = new MyCursorAdapter(this, R.layout.account2, inboxAccounts, from, to, 0);
                listPersonalAdvance = (ListView) findViewById(R.id.inboxListLV);
                count = caPersonal.getCount();
                listPersonalAdvance.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                if (count > 0) {
                    listPersonalAdvance.setAdapter(caPersonal);
               //Swipe n delete message
                    SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                            listPersonalAdvance,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override

                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        val = array.get(position);
                                        db.open();
                                        db.deleteInboxItem(val);
                                        db.close();
                                        recreate();
                                    }

                                    caPersonal.notifyDataSetChanged();
                                }
                            });
                    listPersonalAdvance.setOnTouchListener(touchListener);
                    // Setting this scroll listener is required to ensure that during ListView scrolling,
                    // we don't look for swipes.
                    listPersonalAdvance.setOnScrollListener(touchListener.makeScrollListener());


                    db.close();
                    listPersonalAdvance.setLongClickable(true);
                    listPersonalAdvance
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {
                                    db.open();
                                    db.afterReadMessage(id);
                                    db.close();
                                    String subjectType = ((TextView) view.findViewById(R.id.textViewinboxaccount2)).getText().toString();
                                    if (subjectType.equals("Fuel pilferage alert")) {
                                        Intent intent = new Intent(InboxList.this,
                                                InboxDetails.class);
                                        intent.putExtra("inboxId", id);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(InboxList.this,
                                                InboxCommonDetails.class);
                                        intent.putExtra("inboxId", id);
                                        startActivity(intent);
                                    }


                                }
                            });

                    listPersonalAdvance
                            .setOnItemLongClickListener(new OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent,
                                                               View view, int position, long id) {

                                    alertLongPressed(id);
                                    recreate();
                                    return false;
                                }
                            });
                } else{
                    select.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.alert_nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    noDataLayout.addView(imageView);

                    TextView textView=new TextView(this);
                    textView.setText("NO MESSAGE");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    noDataLayout.addView(textView);
                }
            }
            }catch(SQLiteException e){
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[onCreateView]", e.toString());
            }catch(Exception e){
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[onCreateView]", e.toString());
            }

        super.onStart();
    }

    public void alertLongPressed(final long id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(InboxList.this);
        LayoutInflater inflater = LayoutInflater.from(InboxList.this);
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete selected item ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                db.open();
                db.deleteInboxItem(id);
                db.close();
                alertDialog1.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.dismiss();
            }
        });
        Resources resources =alertDialog1.getContext().getResources();
        int color = resources.getColor(R.color.white);
        alertDialog1.show();
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);
    }

    private class MyCursorAdapter extends SimpleCursorAdapter
    {
        long id;
        public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags)
        {
            super(context, layout, c, from, to, flags);
        }


        public View getView( final int position, View paramView, ViewGroup paramViewGroup)
        {
        view1 = super.getView(position, paramView, paramViewGroup);

            pos=position;
            checkBox = ((CheckBox)view1.findViewById(R.id.checkBox));
            checkBox.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    if (((CheckBox)view).isChecked()) {
                        mCheckedItems.add(pos);
                    }
                    if (!((CheckBox)view).isChecked())
                    {

                        for (int i = 0;i < mCheckedItems.size();i++)
                        {
                            if (mCheckedItems.get(i) == pos)
                            {
                                mCheckedItems.remove(i);
                                checkAll.setChecked(false);
                            }
                        }
                    }

                }
            });

            checkAll.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    CheckBox chk = (CheckBox) view;

                    if (checkAll.isChecked()) {

                        for (int i = 0; i < listPersonalAdvance.getChildCount(); i++) {
                            FrameLayout itemLayout = (FrameLayout) listPersonalAdvance.getChildAt(i);
                            CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                            cb.setChecked(true);
                            mCheckedItems.add(i);

                        }
                       listPersonalAdvance.setOnScrollListener(new AbsListView.OnScrollListener() {
                           @Override
                           public void onScrollStateChanged(AbsListView absListView, int i) {

                           }

                           @Override
                           public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                               for (int i = 0; i < visibleItemCount; i++) {
                                   FrameLayout itemLayout = (FrameLayout) listPersonalAdvance.getChildAt(i);
                                   CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                                   cb.setChecked(true);
                                   mCheckedItems.add(i);
                               }
                           }
                       });

                       }

                    if (!checkAll.isChecked()) {

                        listPersonalAdvance.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView absListView, int i)
                            {

                                try {
                                    for (int i1 = 0; i1 <= mCheckedItems.size(); i1++) {
                                        FrameLayout itemLayout = (FrameLayout) absListView.getChildAt(i1);
                                        CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                                        cb.setChecked(false);
                                        mCheckedItems.remove(i1);
                                    }
                                }
                                catch (Exception e)
                                {
                                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                            + " " + "[!checkAll.isChecked()]", e.toString());
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                            {
                                try {

                                    for (int i = 0; i < visibleItemCount; i++) {

                                        FrameLayout itemLayout = (FrameLayout) listPersonalAdvance.getChildAt(i);
                                        CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                                        cb.setChecked(false);
                                        mCheckedItems.remove(mCheckedItems.get(i));
                                    }
                                }catch (Exception e)
                                {
                                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                            + " " + "[!checkAll.isChecked()-onScroll()]", e.toString());
                                    e.printStackTrace();
                                }

                                        }
                        });
                        mCheckedItems.clear();
                    }
                }
            });
            cancle.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    cancle.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    select.setVisibility(View.VISIBLE);
                    lenearCheck.setVisibility(View.GONE);
                    for (int i = 0; i < InboxList.this.listPersonalAdvance.getChildCount(); i++) {
                            InboxList.this.listPersonalAdvance.getChildAt(i).findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
                        }

                        listPersonalAdvance.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView absListView, int i)
                            {
                                try {
                                    for (int i1 = 0; i1 <= mCheckedItems.size(); i1++) {
                                        FrameLayout itemLayout = (FrameLayout) absListView.getChildAt(i1);
                                        CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                                        cb.setChecked(false);
                                        mCheckedItems.remove(i1);
                                    }
                                }
                                catch (Exception e){
                                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                            + " " + "[cancel.setOnClickListener]", e.toString());
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                            {
                                try {
                                    for (int i = 0; i < visibleItemCount; i++) {
                                        FrameLayout itemLayout = (FrameLayout) listPersonalAdvance.getChildAt(i);
                                        CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checkBox);
                                        cb.setChecked(false);
                                        mCheckedItems.remove(mCheckedItems.get(i));
                                    }
                                }

                                catch (Exception e){
                                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                            + " " + "[listPersonalAdvance.setOnScrollListener-onScroll()]", e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });

                    if(checkBox.isChecked())
                    {
                        for(int i=0;i<mCheckedItems.size();i++)
                        checkBox.setChecked(false);
                    }
                    checkAll.setChecked(false);
                    listPersonalAdvance.clearChoices();
                    mCheckedItems.clear();
                }
            });

            select.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    delete.setVisibility(View.VISIBLE);
                    cancle.setVisibility(View.VISIBLE);
                    lenearCheck.setVisibility(View.VISIBLE);
                    select.setVisibility(View.GONE);

                    for (int i = 0;i < listPersonalAdvance.getChildCount();i++) {
                        listPersonalAdvance.getChildAt(i).findViewById(R.id.checkBox).setVisibility(View.VISIBLE);
                    }
                    mCheckedItems.clear();
                }
            });

            delete.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    if(checkAll.isChecked()==true)
                    {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(InboxList.this);
                        LayoutInflater inflater = LayoutInflater.from(InboxList.this);
                        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
                        builder.setView(dialogView);
                        final AlertDialog alertDialog1 = builder.create();
                        message = (TextView) dialogView.findViewById(R.id.textmsg);
                        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
                        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
                        message.setText("All threads will be deleted.");
                        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
                        alertDialog1.setCustomTitle(v1);

                        ok.setText("DELETE");
                        cancel.setText("CANCEL");
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                db.open();
                                db.deleteAllInboxItem();
                                db.close();
                                recreate();
                                alertDialog1.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog1.dismiss();
                            }
                        });
                        Resources resources =alertDialog1.getContext().getResources();
                        int color = resources.getColor(R.color.white);
                        alertDialog1.show();
                        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
                        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
                        titleDivider.setBackgroundColor(color);

                    }
                    else {
                        if(mCheckedItems.size()==0)
                        {
                            ald.alertDialog(InboxList.this,"Please select message !");
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(InboxList.this);
                            LayoutInflater inflater = LayoutInflater.from(InboxList.this);
                            View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
                            builder.setView(dialogView);
                            final AlertDialog alertDialog1 = builder.create();
                            message = (TextView) dialogView.findViewById(R.id.textmsg);
                            ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
                            cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);

                            if (mCheckedItems.size() == 1) {
                                message.setText("Delete one message ?");
                            } else
                                message.setText("Delete " + mCheckedItems.size() + " messages ?");

                            View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
                            alertDialog1.setCustomTitle(v1);

                            ok.setText("DELETE");
                            cancel.setText("CANCEL");
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    db.open();
                                    for (int i = 0; i < mCheckedItems.size(); i++) {
                                        id = array.get(i);
                                        db.deleteInboxItem(id);
                                    }
                                    db.close();
                                    recreate();
                                    alertDialog1.dismiss();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog1.dismiss();
                                }
                            });
                            Resources resources = alertDialog1.getContext().getResources();
                            int color = resources.getColor(R.color.white);
                            alertDialog1.show();
                            int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
                            View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
                            titleDivider.setBackgroundColor(color);
                        }

                    }
                }
            });
            return view1;
        }
    }
}



