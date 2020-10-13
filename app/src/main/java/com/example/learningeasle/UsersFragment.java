package com.example.learningeasle;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.learningeasle.model.AdapterUsers;
import com.example.learningeasle.model.ModelUsers;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersFragment extends Fragment {
  RecyclerView users;
  AdapterUsers adapterUsers;
  List<ModelUsers> usersList;
    View view;
    public UsersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        users = view.findViewById(R.id.usersrecyclerview);
        users.setHasFixedSize(true);
        users.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersList = new ArrayList<>();
        setHasOptionsMenu(true);
        getAllUsers();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }
                else
                    getAllUsers();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim()))
                    searchUsers(newText);
                else
                    getAllUsers();
                return false;
            }
        });
        //super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchUsers(final String newText) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot db:snapshot.getChildren()){
                    HashMap<Object,String> hashMap = (HashMap<Object, String>) db.getValue();
                    ModelUsers users = new ModelUsers(hashMap.get("Id"),hashMap.get("Name"),hashMap.get("Url"),hashMap.get("email"),hashMap.get("phone"),hashMap.get("status"));
                    if(!hashMap.get("Id").equals(fuser.getUid())) {
                        if(users.getName().toLowerCase().contains(newText)|| users.getEmail().toLowerCase().contains(newText))
                            usersList.add(users);
                    }

                }
                adapterUsers = new AdapterUsers(getActivity(),usersList);
                adapterUsers.notifyDataSetChanged();
                users.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void getAllUsers() {
            //current user;
       final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot db:snapshot.getChildren()){
                    HashMap<Object,String> hashMap = (HashMap<Object, String>) db.getValue();
                    ModelUsers users = new ModelUsers(hashMap.get("Id"),hashMap.get("Name"),hashMap.get("Url"),hashMap.get("email"),hashMap.get("phone"),hashMap.get("status"));
                    if(!hashMap.get("Id").equals(fuser.getUid()))
                    usersList.add(users);

                }
                adapterUsers = new AdapterUsers(getActivity(),usersList);
                users.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//On BackPress load all teh users
    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    getAllUsers();

                    return true;
                }
                return false;
            }
        });
    }
}