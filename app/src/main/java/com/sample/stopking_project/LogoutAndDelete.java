package com.sample.stopking_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class LogoutAndDelete extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_and_delete);

        mAuth = FirebaseAuth.getInstance();

        Button btn_logout = findViewById(R.id.btn_logout);
        Button btn_delete = findViewById(R.id.btn_delete);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        String getEmail = intent.getStringExtra("email");

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃하기
                mAuth.signOut();

                //로그인 화면으로 이동.
                Intent intent = new Intent(LogoutAndDelete.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(LogoutAndDelete.this, "로그아웃에 성공하였습니다.", Toast.LENGTH_SHORT).show();
           }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원 탈퇴하기
                mAuth.getCurrentUser().delete();

                 db.collection("users").whereEqualTo("email",getEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         WriteBatch b=FirebaseFirestore.getInstance().batch();
                         List<DocumentSnapshot> s=queryDocumentSnapshots.getDocuments();
                         for(DocumentSnapshot snapshot:s)
                         {
                             b.delete(snapshot.getReference());
                         }
                         b.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 //로그인 화면으로 이동.
                                 Intent intent = new Intent(LogoutAndDelete.this, LoginActivity.class);
                                 startActivity(intent);
                                 finish();
                                 Toast.makeText(LogoutAndDelete.this, "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 });

            }
        });
    }
}