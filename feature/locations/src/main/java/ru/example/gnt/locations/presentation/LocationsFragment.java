package ru.example.gnt.locations.presentation;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kotlin.jvm.functions.Function1;
import ru.example.gnt.common.base.BaseFragment;
import ru.example.gnt.locations.databinding.LocationsFragmentBinding;

public class LocationsFragment extends Fragment {

    public final static String LOCATIONS_FRAGMENT_TAG = "LOCATIONS_FRAGMENT_TAG";


    public static LocationsFragment createInstance() {
        return new LocationsFragment();
    }

}
