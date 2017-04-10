package com.lisheng.progressv21;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity  {
    private String[] navTitles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition=0;
    private long id;

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            selectItem(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navTitles=getResources().getStringArray(R.array.nav);
        drawerList=(ListView)findViewById(R.id.drawer);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,navTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        if(savedInstanceState==null){
            selectItem(0);
        }

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer) {
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_listfragment,menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void selectItem(int position){
        Fragment fragment;
        switch(position){
            case 1:fragment=new TimerDataFragment();
                break;
            default:
                fragment=new TimerFragment();


        }
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment,"visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setActionBarTitle(position);
        drawerLayout.closeDrawer(drawerList);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener(){
                    public void onBackStackChanged(){
                        FragmentManager fragMan=getFragmentManager();
                        Fragment fragment=fragMan.findFragmentByTag("visible_fragment");
                        if(fragment instanceof TimerFragment){
                            currentPosition=0;


                        }
                        if(fragment instanceof TimerDataFragment){
                            currentPosition=1;
                        }
                        setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition,true);
                    }
                }
        );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setActionBarTitle(int position){
        String title;
        if(position==0){
            title=getResources().getString(R.string.app_name);
        }else{
            title=navTitles[position];
        }
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen=drawerLayout.isDrawerOpen(drawerList);
        FragmentManager fragMan=getFragmentManager();
        Fragment fragment=fragMan.findFragmentByTag("visible_fragment");
        if(fragment instanceof TimerFragment){
           MenuItem menuItem= menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()){
            case R.id.action_delete:
                Toast toast= Toast.makeText(this,"delete button",Toast.LENGTH_SHORT);
                toast.show();
                SQLiteOpenHelper databaseHelper=new TimerDatabaseHelper(this);
                SQLiteDatabase db=databaseHelper.getWritableDatabase();
                TimerDataFragment.deleteEntry(db,TimerDataFragment.currentLongId);

                Fragment fragment=new TimerDataFragment();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment,"visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                return true;
            default:return super.onOptionsItemSelected(item);
        }


    }



}
