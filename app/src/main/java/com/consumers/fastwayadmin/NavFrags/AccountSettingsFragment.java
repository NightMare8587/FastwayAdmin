package com.consumers.fastwayadmin.NavFrags;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.consumers.fastwayadmin.ListViewActivity.BlockedUsersList.BlockedUsers;
import com.consumers.fastwayadmin.ListViewActivity.CashTrans.CashTransactions;
import com.consumers.fastwayadmin.ListViewActivity.MyAccount;
import com.consumers.fastwayadmin.ListViewActivity.MyOrdersTransactions;
import com.consumers.fastwayadmin.MyService;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class AccountSettingsFragment extends PreferenceFragmentCompat {
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String UID;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        UID = auth.getUid();
        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso);
        Preference account = findPreference("myAccount");
        Preference blockedUsers = findPreference("blockedUsers");
        Preference myTrans = findPreference("myTrans");
        Preference logout = findPreference("logoutNow");
        Preference premium = findPreference("premium");
        Preference terms = findPreference("termsConditions");
        SwitchPreferenceCompat takeaway = findPreference("TakeAwaySwitch");
        SwitchPreferenceCompat tableOrder = findPreference("tableBookingAllowedSwitch");

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor sharedEdit = sharedPreferences.edit();
        takeaway.setChecked(sharedPreferences.getString("TakeAwayAllowed", "").equals("yes"));
        tableOrder.setChecked(sharedPreferences.getString("TableBookAllowed", "").equals("yes"));
        terms.setOnPreferenceClickListener(preference -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.websitepolicies.com/policies/view/CpwDZziF"));
            startActivity(browserIntent);
            return true;
        });
        Preference privacy = findPreference("privacyPolicy");

        privacy.setOnPreferenceClickListener(preference -> {
            Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fastway.flycricket.io/privacy.html"));
            startActivity(privacyIntent);
            return true;
        });
        premium.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), FastwayPremiums.class));
            return true;
        });

        logout.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Logout")
                    .setMessage("Do you wanna logout?")
                    .setPositiveButton("Yes", (dialogInterface, i1) -> {
                        SharedPreferences settings = requireContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(settings.getString("state","")).child(settings.getString("locality","")).child(UID);
                        databaseReference.child("status").setValue("offline");
                        databaseReference.child("acceptingOrders").setValue("no");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Objects.requireNonNull(UID));
                        requireContext().stopService(new Intent(requireContext(), MyService.class));
                        SharedPreferences stopServices = requireActivity().getSharedPreferences("Stop Services", MODE_PRIVATE);
                        SharedPreferences.Editor editor = stopServices.edit();
                        editor.putString("online","false");
                        editor.apply();
                        auth.signOut();
                        googleSignInClient.signOut().addOnCompleteListener((Activity) requireContext(), task -> {
                        });

                        settings.edit().clear().apply();

                        SharedPreferences res = requireContext().getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                        res.edit().clear().apply();

                        SharedPreferences intro = requireContext().getSharedPreferences("IntroAct", MODE_PRIVATE);
                        intro.edit().clear().apply();

                        SharedPreferences storeOrders = requireContext().getSharedPreferences("StoreOrders", MODE_PRIVATE);
                        storeOrders.edit().clear().apply();

                        SharedPreferences location = requireContext().getSharedPreferences("LocationMaps", MODE_PRIVATE);
                        location.edit().clear().apply();

                        SharedPreferences cashCommission = requireContext().getSharedPreferences("CashCommission", MODE_PRIVATE);
                        cashCommission.edit().clear().apply();

                        SharedPreferences RestaurantTrackingDaily = requireContext().getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
                        RestaurantTrackingDaily.edit().clear().apply();

                        SharedPreferences RestaurantTrackRecords = requireContext().getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
                        RestaurantTrackRecords.edit().clear().apply();

                        SharedPreferences DishAnalysis = requireContext().getSharedPreferences("DishAnalysis", MODE_PRIVATE);
                        DishAnalysis.edit().clear().apply();

                        startActivity(new Intent(getActivity(), SplashScreen.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialogInterface, i15) -> dialogInterface.dismiss()).create();

            builder.show();
            return true;
        });

        myTrans.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            alert.setTitle("Choose");
            alert.setMessage("Choose one option from below");
            alert.setPositiveButton("Show Online Transactions", (dialogInterface, i12) -> {
                dialogInterface.dismiss();
                startActivity(new Intent(getActivity(), MyOrdersTransactions.class));
            }).setNegativeButton("Show Cash Transactions", (dialogInterface, i13) -> {
                dialogInterface.dismiss();
                startActivity(new Intent(getActivity(), CashTransactions.class));
            }).setNeutralButton("Cancel", (dialogInterface, i14) -> dialogInterface.dismiss()).create();

            alert.show();
            return true;
        });

        blockedUsers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(requireContext(), BlockedUsers.class));
                return true;
            }
        });
        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(requireContext(), MyAccount.class));
                return true;
            }
        });

        takeaway.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences login = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
            Toast.makeText(requireContext(), "" + newValue, Toast.LENGTH_SHORT).show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(UID);
            boolean isChecked = (boolean) newValue;
            if(isChecked){
                databaseReference.child("TakeAwayAllowed").setValue("yes");
//                        takeaway.setChecked(true);
                sharedEdit.putString("TakeAwayAllowed","yes");
            }else{
                databaseReference.child("TakeAwayAllowed").setValue("no");
//                        takeaway.setChecked(false);
                sharedEdit.putString("TakeAwayAllowed","no");

            }

            sharedEdit.apply();
            return true;
        });

        tableOrder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Toast.makeText(requireContext(), "" + UID, Toast.LENGTH_SHORT).show();

                    SharedPreferences login = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
//                    Toast.makeText(requireContext(), "" + newValue, Toast.LENGTH_SHORT).show();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(UID);
                    boolean isChecked = (boolean) newValue;
                    if(isChecked){
                        databaseReference.child("TableBookAllowed").setValue("yes");
//                        takeaway.setChecked(true);
                        sharedEdit.putString("TableBookAllowed","yes");
                    }else{
                        databaseReference.child("TableBookAllowed").setValue("no");
//                        takeaway.setChecked(false);
                        sharedEdit.putString("TableBookAllowed","no");

                    }

                    sharedEdit.apply();
//                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                    boolean isChecked = pref.getBoolean("tableBookingAllowedSwitch",false);
//
//                    if(isChecked){
//
//                    }
//                    if(preference instanceof SwitchPreferenceCompat && newValue.equals("false")){
//                        databaseReference.child("TableBookAllowed").setValue("no");
////                        takeaway.setChecked(false);
//                        sharedEdit.putString("TableBookAllowed","no");
//                    }else{
//                        databaseReference.child("TableBookAllowed").setValue("yes");
////                        takeaway.setChecked(true);
//                        sharedEdit.putString("TableBookAllowed","yes");
//                    }
                    sharedEdit.apply();

                return true;
            }
        });
    }
}