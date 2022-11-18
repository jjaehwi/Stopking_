package com.sample.stopking_project;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class alert_scaner {

    private Context context;
    private String text;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String getEmail;

    public alert_scaner(Statistics context, String text, String getEmail) {
        this.context = context;
        this.text = text;
        this.getEmail = getEmail;
    }

    private ModifyReturnListener modifyReturnListener;

    public interface ModifyReturnListener{
        void afterModify(String content);
    }
    public void setModifyReturnListener(ModifyReturnListener modifyReturnListener){
        this.modifyReturnListener = modifyReturnListener;
    }
    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);  // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE); // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.alert_scaner);  // 커스텀 다이얼로그를 노출한다.
        dlg.show(); // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        TextView conformBtn = (TextView) dlg.findViewById(R.id.conformBtn); // 확인 버튼
        TextView backBtn = (TextView) dlg.findViewById(R.id.backBtn);
        EditText scaneET = (EditText) dlg.findViewById(R.id.scaneET);
        scaneET.setText(text);

        backBtn.setOnClickListener(new View.OnClickListener() { //취소 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                dlg.onBackPressed();
            }
        });

        conformBtn.setOnClickListener(new View.OnClickListener() { // 수정 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("users").document(getEmail);
                String ModifyedText = scaneET.getText().toString(); //스케너에 있는 텍스트 String으로 가져오기
                modifyReturnListener.afterModify(ModifyedText);
                dlg.onBackPressed();
                // db 지정 필드에 값 업데이트하기!
                docRef.update("drink_bank",scaneET.getText().toString());
            }
        });

    }
}
