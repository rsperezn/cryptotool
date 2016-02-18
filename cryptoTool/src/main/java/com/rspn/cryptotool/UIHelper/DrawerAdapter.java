package com.rspn.cryptotool.UIHelper;

import java.util.List;
import com.rspn.cryptotool.model.NavigationItem;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rspn.cryptotool.R;
 
public class DrawerAdapter extends ArrayAdapter<NavigationItem> {
 
      Context context;
      List<NavigationItem> drawerItemList;
      int layoutResID;
 
      public DrawerAdapter(Context context,List<NavigationItem> listItems) {
            super(context, R.layout.sigle_row_navigation, listItems);
            this.context = context;
            this.drawerItemList = listItems;
            this.layoutResID = R.layout.sigle_row_navigation;
 
      }
 
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
 
            DrawerItemHolder drawerHolder;
            View view = convertView;
 
            if (view == null) {
                  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                  drawerHolder = new DrawerItemHolder();
 
                  view = inflater.inflate(layoutResID, parent, false);
                  drawerHolder.ItemName = (TextView) view.findViewById(R.id.title_icon_navigation);
                  drawerHolder.icon = (ImageView) view.findViewById(R.id.icon_navigation);
 
                  view.setTag(drawerHolder);
 
            } else {
                  drawerHolder = (DrawerItemHolder) view.getTag();
 
            }
 
            NavigationItem dItem = (NavigationItem) this.drawerItemList.get(position);
 
            drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
            drawerHolder.ItemName.setText(dItem.getItemName());
 
            return view;
      }
 
      private static class DrawerItemHolder {
            TextView ItemName;
            ImageView icon;
      }
}
