package com.sample.stopking_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String getEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.btn_back);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");

        // 뒤로 가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });

    }

    //내 정보 클릭 시
    public void UserClick(View v){
        Intent intent = new Intent(SettingActivity.this, MyInfoActivity.class);
        startActivity(intent);

    }

    //금주 / 금연 변경 클릭 시 처리 함수
    public void ChangeMenu(View v) {
        int flag;
        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().getString("start_smoke") == null) {
                    Intent intent = new Intent(SettingActivity.this, ChangeToSmoke.class);
                    intent.putExtra("email", getEmail);
                    startActivity(intent);
                }
                else if(task.getResult().getString("stop_drink") == null){
                    Intent intent = new Intent(SettingActivity.this, ChangeToDrink.class);
                    intent.putExtra("email", getEmail);
                    startActivity(intent);
                }
                else if ((task.getResult().getString("flag")).compareTo("drink") == 0) { // 금주 테마일 경우
                    Intent intent = new Intent(SettingActivity.this, Smoke_MainActivity.class);
                    startActivity(intent);
                    docRef.update("flag","smoke");
                    finish();
                } else if ((task.getResult().getString("flag")).compareTo("smoke") == 0) { // 금연 테마일 경우
                    Intent intent = new Intent(SettingActivity.this, Drink_MainActivity.class);
                    startActivity(intent);
                    docRef.update("flag","drink");
                    finish();
                }
            }
        });
    }
    // 금연 / 금주 초기화 클릭 시 처리 함수
    public void initializeMenu(View v) {

    }

    //로그아웃 클릭 시
    public void user_logout(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("주의");
        builder.setMessage("정말 로그아웃을 하시겠습니까?");
        builder.setIcon(R.drawable.caution);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //로그아웃하기
                mAuth.signOut();

                //로그인 화면으로 이동.
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(SettingActivity.this, "로그아웃에 성공하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //아무 일도 발생하지 않는다.
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 회원 탈퇴 클릭 시
    public void user_delete(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("주의");
        builder.setMessage("회원탈퇴 시 앱이 종료되며, 앱을 재실행해주셔야 합니다. 정말 회원탈퇴를 하시겠습니까?");
        builder.setIcon(R.drawable.caution);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                                // Toast.makeText(SettingActivity.this, "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //로그인 화면으로 이동.
                Toast.makeText(SettingActivity.this, "회원탈퇴가 정상적으로 처리되었습니다. 앱 이용을 계속 원하신다면 앱을 재시작해주세요", Toast.LENGTH_SHORT).show();
                ActivityCompat.finishAffinity(SettingActivity.this);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //아무 일도 발생하지 않는다.
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}