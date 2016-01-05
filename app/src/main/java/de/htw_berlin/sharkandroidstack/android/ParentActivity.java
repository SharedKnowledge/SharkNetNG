package de.htw_berlin.sharkandroidstack.android;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.system_modules.intro.IntroActivity;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class ParentActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    public static final int LAYOUT_OPTION_RESOURCE = 1;
    public static final int LAYOUT_OPTION_FRAGMENT = 2;
    public static final int LAYOUT_OPTION_NULL = -1;

    public static final int UNIQUE_GROUP_ID_SYSTEM_MODULES = 37820;
    public static final int UNIQUE_GROUP_ID_MODULES = 67820;

    public static final String EXTRA_MENU_ITEM_ID = "EXTRA_MENU_ITEM_ID";

    private int layoutInUse = LAYOUT_OPTION_NULL;
    private int optionsMenuResource = 0;

    private Menu menu;
    private int menuItemId;

    private static View.OnClickListener returnToIntroClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.system_parent_activity);

        overridePendingTransition(R.anim.in_left_to_right, R.anim.out_right_to_left);

        menu = installActionBarAndSideNavDrawer();
        fillSideNavDrawerWithModules(menu, SideNav.system_modules, UNIQUE_GROUP_ID_SYSTEM_MODULES, R.string.sidenav_menu_cat_system);
        fillSideNavDrawerWithModules(menu, SideNav.modules, UNIQUE_GROUP_ID_MODULES, R.string.sidenav_menu_cat_modules);
    }

    private Menu installActionBarAndSideNavDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.sidenav_drawer_open, R.string.sidenav_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.sidenav_view);
        View viewById = navigationView.getHeaderView(0).findViewById(R.id.sidenav_header_icon);
        viewById.setOnClickListener(returnToIntroClickListener);
        navigationView.setNavigationItemSelectedListener(this);
        return navigationView.getMenu();
    }

    private void fillSideNavDrawerWithModules(Menu menu, String[][] entries, int uniqueGroupId, int categoryNameResource) {
        SubMenu test = menu.addSubMenu(categoryNameResource);
        for (int i = 0; i < entries.length; i++) {
            String[] entry = entries[i];
            int uniqueItemId = i + uniqueGroupId;
            test.add(uniqueGroupId, uniqueItemId, i, entry[0]);
        }
        menu.setGroupCheckable(uniqueGroupId, true, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        menuItemId = getIntent().getIntExtra(EXTRA_MENU_ITEM_ID, -1);

        if (menuItemId < 0) {
            return;
        }

        int size = menu.size();
        for (int i = 0; i < size; i++) {
            SubMenu submenu = menu.getItem(i).getSubMenu();
            int size1 = submenu.size();
            for (int j = 0; j < size1; j++) {
                MenuItem item = submenu.getItem(j);
                if (item.getItemId() == menuItemId) {
                    item.setChecked(true);
                } else {
                    item.setChecked(false);
                }
            }
        }
    }

    protected void setOptionsMenu(int resource) {
        optionsMenuResource = resource;
    }

    private void checkIfLayoutIsUsed(int layoutOption) {
        if (layoutInUse != LAYOUT_OPTION_NULL) {
            throw new IllegalStateException("Layout already set.");
        }
        layoutInUse = layoutOption;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuResource <= 0) {
            return false;
        }
        getMenuInflater().inflate(optionsMenuResource, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == menuItemId) {
            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        int normalizedItemId = item.getItemId() - item.getGroupId();
        String[][] entries = item.getGroupId() == UNIQUE_GROUP_ID_SYSTEM_MODULES ? SideNav.system_modules : SideNav.modules;
        String[] entry = entries[normalizedItemId];
        String entryName = entry[0];
        String className = entry[1];

        if (className == null) {
            return false;
        }

        try {
            Class aClass = Class.forName(getBaseContext().getPackageName() + "." + className);
            Intent intent = new Intent(this, aClass);
            intent.putExtra(EXTRA_MENU_ITEM_ID, item.getItemId());
            this.startActivity(intent);
            return true;
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Class not found for item '" + entryName + "'", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No activity declared in AndroidManifest for item " + entryName + "'", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return false;
    }

    protected void setLayoutResource(int resource) {
        checkIfLayoutIsUsed(LAYOUT_OPTION_RESOURCE);

        View includeContainer = findViewById(R.id.include);

        RelativeLayout rl = (RelativeLayout) includeContainer;
        ViewGroup rootView = (ViewGroup) includeContainer.getRootView();
        View inflate = getLayoutInflater().inflate(resource, rootView, false);
        rl.addView(inflate);
    }

    protected void setFragment(Fragment fragment) {
        checkIfLayoutIsUsed(LAYOUT_OPTION_FRAGMENT);

        getFragmentManager().beginTransaction().replace(R.id.include, fragment).commit();
    }
}
