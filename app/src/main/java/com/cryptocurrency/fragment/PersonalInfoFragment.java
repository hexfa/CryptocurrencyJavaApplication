package com.cryptocurrency.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cryptocurrency.MainActivity;
import com.cryptocurrency.R;
import com.cryptocurrency.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import me.ibrahimsn.lib.SmoothBottomBar;


public class PersonalInfoFragment extends Fragment {
    FragmentProfileBinding fragmentProfileBinding;
    MainActivity mainActivity;
    SmoothBottomBar smoothBottomBar;
    String picturePath;
    String ImgFromStore;
    String fName, lName, email;

    ActivityResultLauncher<Intent> GetContext = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = requireActivity().getContentResolver().query(selectImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        fragmentProfileBinding.roundedImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    }
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentProfileBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_profile, container, false);
        readToDataStore();
        setDefaultValue();
        getPhotoFromGallery();
        setupSaveBtn();
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //bottom navigation view becomes Gone
        smoothBottomBar = requireActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.GONE);

        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.profileFragment).build();
        Toolbar profileToolbar = view.findViewById(R.id.profiletoolbar);
        NavigationUI.setupWithNavController(profileToolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.profileFragment) {
                profileToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
                profileToolbar.setTitle("Profile");
            }
        });
    }

    private void getPhotoFromGallery() {
        fragmentProfileBinding.changeImg.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                GetContext.launch(cameraIntent);
            }
        });
    }

    private void setupSaveBtn() {
        fragmentProfileBinding.saveBtn.setOnClickListener(view -> {
            writeToDataStore();
            Snackbar.make(fragmentProfileBinding.ProfileCon, "picture saved", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void readToDataStore() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ImgFromStore = sharedPreferences.getString("profileImg", null);
        fName = sharedPreferences.getString("firstName", "");
        lName = sharedPreferences.getString("lastName", "");
        email = sharedPreferences.getString("email", "");
    }

    private void writeToDataStore() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (picturePath != null) {
            editor.putString("profileImg", encodeTobase64(BitmapFactory.decodeFile(picturePath)));
        }
        editor.putString("firstName", fragmentProfileBinding.editTextTextPersonName.getText().toString());
        editor.putString("lastName", fragmentProfileBinding.editTextTextPersonName2.getText().toString());
        editor.putString("email", fragmentProfileBinding.editTextTextPersonName3.getText().toString());
        editor.apply();
    }


    public static String encodeTobase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public static Bitmap decodeBase64(String string) {
        byte[] decodeByte = Base64.decode(string, 0);
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    //when destroy this fragment, bottom navigation becomes visible
    @Override
    public void onDestroy() {
        super.onDestroy();
        smoothBottomBar.setVisibility(View.VISIBLE);
    }

    private void setDefaultValue() {
        if (ImgFromStore == null) {
            fragmentProfileBinding.roundedImageView.setImageResource(R.drawable.profile_placeholder);
        } else {
            fragmentProfileBinding.roundedImageView.setImageBitmap(decodeBase64(ImgFromStore));
        }
        fragmentProfileBinding.editTextTextPersonName.setText(fName);
        fragmentProfileBinding.editTextTextPersonName2.setText(lName);
        fragmentProfileBinding.editTextTextPersonName3.setText(email);
    }
}

