package com.optlab.banhangso.features.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.firebase.auth.FirebaseAuth;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentHomeBinding;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements View.OnClickListener {

    @Inject FirebaseAuth firebaseAuth;
    @Inject PreferenceRepository preferenceRepository;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPrefRepo(preferenceRepository);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.bnv, navController);
        setCurrentDate();
    }

    @Override
    public void onClick(View v) {
        NavController controller = Navigation.findNavController(v);
        int viewId = v.getId();
        if (viewId == R.id.ib_stores) {
            NavOptions options = NavigationUtils.getNavOptions(R.id.homeFragment, true);
            NavDirections action = HomeFragmentDirections.actionToSelectFragment();
            controller.navigate(action, options);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentDate() {
        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = LocalDate.now(zoneId).format(formatter);
        binding.tvDay.setText(getString(R.string.today) + ": " + currentDate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onLogOutImageViewClick(View view) {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
            preferenceRepository.clearPreferences();
            NavOptions options = NavigationUtils.getNavOptions(R.id.homeFragment, true);
            Navigation.findNavController(view)
                    .navigate(R.id.action_to_authentication, null, options);
        }
    }
}
