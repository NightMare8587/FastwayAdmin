package com.consumers.fastwayadmin.NavFrags;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.consumers.fastwayadmin.ListViewActivity.BlockedUsersList.BlockedUsers;
import com.consumers.fastwayadmin.ListViewActivity.CashTrans.CashTransactions;
import com.consumers.fastwayadmin.ListViewActivity.MyAccount;
import com.consumers.fastwayadmin.ListViewActivity.MyOrdersTransactions;
import com.consumers.fastwayadmin.ListViewActivity.ViewExcelSheets;
import com.consumers.fastwayadmin.MyService;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashTransactionCommissionActivity;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.NavFrags.Reviews.RestaurantsReviews;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class AccountFrag extends Fragment {
    ListView listView;
    String[] names = {"My Account","Blocked Users","My Transactions","Logout","Terms And Conditions","Privacy policy","Restaurant Reviews","Cash Transaction Commission","Fastway Premium","Transactions Excel Sheet"};
    GoogleSignInClient googleSignInClient;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch,takeawaySwitch,tableSwitch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String UID;
    FirebaseAuth auth;
    int count = 0;
    boolean pressed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_frag,container,false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                count++;
                pressed = true;
                Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();

                if(count == 2 && pressed)
                    requireActivity().finish();

                new Handler().postDelayed(() -> {
                    pressed = false;
                    count = 0;
                },2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid() + "";
        aSwitch = view.findViewById(R.id.storeImagesinPhoneSwitch);
        takeawaySwitch = view.findViewById(R.id.takeaAwayAllowedOrNotSwitch);
        tableSwitch = view.findViewById(R.id.tableBookAllowedOrNotSwitch);
         sharedPreferences = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
         editor = sharedPreferences.edit();

        if(sharedPreferences.getString("storeInDevice","").equals("yes"))
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);

        if(sharedPreferences.getString("TakeAwayAllowed","").equals("yes"))
            takeawaySwitch.setChecked(true);
        else
            takeawaySwitch.setChecked(false);

        if(sharedPreferences.getString("TableBookAllowed","").equals("yes"))
            tableSwitch.setChecked(true);
        else
            tableSwitch.setChecked(false);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.list, names);
        listView.setAdapter(arrayAdapter);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            int id = (int) adapterView.getItemIdAtPosition(i);
            switch (id){
                case 0:
                    startActivity(new Intent(getActivity(), MyAccount.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), BlockedUsers.class));
                    break;
                case 2:

                    AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                    alert.setTitle("Choose");
                    alert.setMessage("Choose one option from below");
                    alert.setPositiveButton("Show Online Transactions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(getActivity(), MyOrdersTransactions.class));
                        }
                    }).setNegativeButton("Show Cash Transactions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(getActivity(), CashTransactions.class));
                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();

                    alert.show();
                    break;
                case 3:
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Logout")
                            .setMessage("Do you wanna logout?")
                            .setPositiveButton("Yes", (dialogInterface, i1) -> {
                                SharedPreferences settings = view1.getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
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
                                googleSignInClient.signOut().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });

                                settings.edit().clear().commit();

                                SharedPreferences res = view1.getContext().getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                                res.edit().clear().commit();

                                SharedPreferences intro = view1.getContext().getSharedPreferences("IntroAct", MODE_PRIVATE);
                                intro.edit().clear().commit();

                                SharedPreferences storeOrders = view1.getContext().getSharedPreferences("StoreOrders", MODE_PRIVATE);
                                storeOrders.edit().clear().commit();

                                SharedPreferences location = view1.getContext().getSharedPreferences("LocationMaps", MODE_PRIVATE);
                                location.edit().clear().commit();

                                SharedPreferences cashCommission = view1.getContext().getSharedPreferences("CashCommission", MODE_PRIVATE);
                                cashCommission.edit().clear().commit();

                                SharedPreferences RestaurantTrackingDaily = view1.getContext().getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
                                RestaurantTrackingDaily.edit().clear().commit();

                                SharedPreferences RestaurantTrackRecords = view1.getContext().getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
                                RestaurantTrackRecords.edit().clear().commit();

                                SharedPreferences DishAnalysis = view1.getContext().getSharedPreferences("DishAnalysis", MODE_PRIVATE);
                                DishAnalysis.edit().clear().commit();

                                startActivity(new Intent(getActivity(), SplashScreen.class));
                                getActivity().finish();
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();

                    builder.show();
                    break;

                case 4:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.websitepolicies.com/policies/view/CpwDZziF"));
                    startActivity(browserIntent);
                    break;
                case 5:
                    Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fastway.flycricket.io/privacy.html"));
                    startActivity(privacyIntent);
                    break;
                case 6:
                startActivity(new Intent(requireContext(), RestaurantsReviews.class));
                break;
                case 7:
                startActivity(new Intent(requireContext(), CashTransactionCommissionActivity.class));
                break;
                case 8:
                    startActivity(new Intent(requireContext(), FastwayPremiums.class));
                    break;
                case 9:
                    startActivity(new Intent(requireContext(), ViewExcelSheets.class));
                    break;
            }
        });


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(requireContext(), "Images Will be stored in device storage", Toast.LENGTH_SHORT).show();
                    editor.putString("storeInDevice","yes");
                    editor.apply();
                } else {
                    Toast.makeText(requireContext(), "Images Will not be stored in device storage", Toast.LENGTH_SHORT).show();
                    editor.putString("storeInDevice","no");
                    editor.apply();
                }
            }
        });

        takeawaySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                takeawaySwitch.setChecked(true);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
                databaseReference.child("TakeAwayAllowed").setValue("yes");
                editor.putString("TakeAwayAllowed","yes");
                editor.apply();
                Toast.makeText(requireContext(), "You will now receive TakeAway Orders", Toast.LENGTH_SHORT).show();
            }else{
                takeawaySwitch.setChecked(false);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
                databaseReference.child("TakeAwayAllowed").setValue("no");
                editor.putString("TakeAwayAllowed","no");
                editor.apply();
                Toast.makeText(requireContext(), "You will not receive TakeAway Orders", Toast.LENGTH_SHORT).show();
            }
        });

        tableSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                tableSwitch.setChecked(true);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
                databaseReference.child("TableBookAllowed").setValue("yes");
                editor.putString("TableBookAllowed","yes");
                editor.apply();
                Toast.makeText(requireContext(), "Table Booking Enabled", Toast.LENGTH_SHORT).show();
            }else{
                tableSwitch.setChecked(false);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
                databaseReference.child("TableBookAllowed").setValue("no");
                editor.putString("TableBookAllowed","no");
                editor.apply();
                Toast.makeText(requireContext(), "Table Booking Disabled", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
