package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.ListItemHomepageShortcutBinding;
import com.optlab.banhangso.data.model.QuickShortcut;
import com.optlab.banhangso.ui.home.viewmodel.QuickShortcutViewModel;

import java.util.List;

public class QuickShortcutAdapter extends BaseAdapter {

    private final QuickShortcutViewModel viewModel;
    private List<QuickShortcut> quickShortcutList;

    public QuickShortcutAdapter(QuickShortcutViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public QuickShortcutAdapter(QuickShortcutViewModel viewModel, List<QuickShortcut> quickShortcutList) {
        this.viewModel = viewModel;
        this.quickShortcutList = quickShortcutList;
    }

    public void setShortcutList(List<QuickShortcut> quickShortcutList) {
        this.quickShortcutList = quickShortcutList;
    }

    @Override
    public int getCount() {
        return quickShortcutList.isEmpty() ? 0 : quickShortcutList.size();
    }

    @Override
    public Object getItem(int position) {
        return quickShortcutList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ListItemHomepageShortcutBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.list_item_homepage_shortcut, parent, false
            );
            viewHolder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(v -> viewModel.setClickedPosition(position));

        QuickShortcut currentShortcut = quickShortcutList.get(position);
        viewHolder.bind(currentShortcut);
        return convertView;
    }

    public static class ViewHolder {
        private final ListItemHomepageShortcutBinding binding;

        public ViewHolder(ListItemHomepageShortcutBinding binding) {
            this.binding = binding;
        }

        public void bind(QuickShortcut shortcut) {
            binding.setShortcut(shortcut);
            binding.executePendingBindings();
        }
    }
}
