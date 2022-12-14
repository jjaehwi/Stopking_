package com.sample.stopking_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String getEmail;
    AlertDialog initAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.btn_back);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        DocumentReference docRef = db.collection("users").document(getEmail);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //금주 정보만 있을 때
                if (task.getResult().getString("start_smoke") == null) {
                    builder.setTitle("주의");
                    builder.setMessage("현재 금연 정보가 존재하지 않습니다. \n 금연 정보를 입력하시곘습니까?");
                    builder.setIcon(R.drawable.caution);

                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SettingActivity.this, ChangeToSmoke.class);
                            startActivity(intent);
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
                //금연 정보만 있을 때
                else if(task.getResult().getString("stop_drink") == null){
                    builder.setTitle("주의");
                    builder.setMessage("현재 금주 정보가 존재하지 않습니다. \n 금주 정보를 입력하시곘습니까?");
                    builder.setIcon(R.drawable.caution);

                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SettingActivity.this, ChangeToDrink.class);
                            startActivity(intent);
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
                // 금주테마일 경우 금연테마로 변경
                else
                {
                    builder.setTitle("주의");
                    if ((task.getResult().getString("flag")).compareTo("drink") == 0)
                        builder.setMessage("금주에서 금연테마로 변경하시겠습니까?");
                    else if ((task.getResult().getString("flag")).compareTo("smoke") == 0)
                        builder.setMessage("금연에서 금주테마로 변경하시겠습니까?");

                    builder.setIcon(R.drawable.caution);

                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if ((task.getResult().getString("flag")).compareTo("drink") == 0)
                                docRef.update("flag","smoke");
                            else if ((task.getResult().getString("flag")).compareTo("smoke") == 0)
                                docRef.update("flag","drink");

                            Intent intent = new Intent(SettingActivity.this, LoadingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
        });
    }
    public void showSuccessDialog(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("초기화가 완료되었습니다.")
                .setMessage("다음 번엔 성공할 수 있을거에요!")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SettingActivity.this, LoadingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

        AlertDialog successAlertDialog = builder2.create();
        successAlertDialog.show();
    }


    // 금연 / 금주 초기화 클릭 시 처리 함수
    public void initializeMenu(View v) {
        DocumentReference docRef = db.collection("users").document(getEmail);

//        DocumentReference docRef = db.collection("users").document(getEmail);
//        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String flag = task.getResult().getString("flag");

                if(flag.compareTo("drink")==0){ // 금주일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("주의")
                            .setMessage("음주를 하셨나요? (금주 정보가 초기화됩니다.)")
                            .setIcon(R.drawable.caution)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    String getDate = dateFormat.format(date);
                                    docRef.update("stop_drink",getDate);
                                    showSuccessDialog();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    initAlertDialog = builder.create();
                    if(!initAlertDialog.isShowing())initAlertDialog.show();
                }
                else if(flag.compareTo("smoke")==0){ // 금연일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("주의")
                            .setMessage("흡연을 하셨나요? (금연 정보가 초기화됩니다.)")
                            .setIcon(R.drawable.caution)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    String getDate = dateFormat.format(date);
                                    docRef.update("stop_smoke",getDate);
                                    showSuccessDialog();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    initAlertDialog = builder.create();
                    if(!initAlertDialog.isShowing())initAlertDialog.show();
                }
            }

        });

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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        builder.setMessage("회원님의 정보가 모두 삭제됩니다.\n 정말 회원탈퇴를 하시겠습니까?");
        builder.setIcon(R.drawable.caution);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 회원 탈퇴하기
                mAuth.getCurrentUser().delete();
                mAuth.signOut();

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
                Toast.makeText(SettingActivity.this, "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                //로그인 화면으로 이동.
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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