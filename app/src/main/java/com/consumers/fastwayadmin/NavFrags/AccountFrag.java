package com.consumers.fastwayadmin.NavFrags;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.ListView;
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
import com.consumers.fastwayadmin.MyService;
import com.consumers.fastwayadmin.NavFrags.Reviews.RestaurantsReviews;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class AccountFrag extends Fragment {
    ListView listView;
    String[] names = {"My Account","Blocked Users","My Transactions","Logout","Terms And Conditions","Privacy policy","Restaurant Reviews"};
    GoogleSignInClient googleSignInClient;

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
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(Objects.requireNonNull(auth.getUid()));
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
                                SharedPreferences settings = view1.getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
                                settings.edit().clear().commit();

                                SharedPreferences res = view1.getContext().getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                                res.edit().clear().commit();

                                SharedPreferences intro = view1.getContext().getSharedPreferences("IntroAct", MODE_PRIVATE);
                                intro.edit().clear().commit();

                                SharedPreferences location = view1.getContext().getSharedPreferences("LocationMaps", MODE_PRIVATE);
                                location.edit().clear().commit();

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
            }
        });
    }
}
