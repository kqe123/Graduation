package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

public class Register2Activity extends AppCompatActivity {
    Button setLikingBtn;
    RadioGroup rgroupDegree, rgroupPrice, rgroupCarbonated, rgroupRecom1, rgroupRecom2, rgroupRecom3;
    int sweetness, bitterness;
    String degree, price;
    Boolean carbonated;
    String recom1st, recom2nd, recom3rd;
    RatingBar sweet,bitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        rgroupDegree = findViewById(R.id.rgroupDegree); // 도수 radiogroup
        rgroupPrice = findViewById(R.id.rgroupPrice);   // 가격 radiogroup
        rgroupCarbonated = findViewById(R.id.rgroupCarbonated); // 탄산 radiogroup
        rgroupRecom1 = findViewById(R.id.recommend1);
        rgroupRecom2 = findViewById(R.id.recommend2);
        rgroupRecom3 = findViewById(R.id.recommend3);
        setLikingBtn = findViewById(R.id.btn_set_liking);
        sweet = findViewById(R.id.sweetness); // 별 위젯(sweetness)
        bitter = findViewById(R.id.bitterness); // 별 위젯(bitterness)




        setLikingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // radioGroup중 선택된 radioButton의 ID (없다면 -1이 반환된다.)
                int selectedDegreeId = rgroupDegree.getCheckedRadioButtonId();
                int selectedPriceId = rgroupPrice.getCheckedRadioButtonId();
                int selectedCarbonId = rgroupCarbonated.getCheckedRadioButtonId();
                int selectedRecom1Id = rgroupRecom1.getCheckedRadioButtonId();
                int selectedRecom2Id = rgroupRecom2.getCheckedRadioButtonId();
                int selectedRecom3Id = rgroupRecom3.getCheckedRadioButtonId();

                if (selectedDegreeId == -1 || selectedPriceId == -1 || selectedCarbonId == -1 || selectedRecom1Id == -1 || selectedRecom2Id == -1 || selectedRecom3Id == -1) {
                    // 도수, 가격, 탄산여부, 1~3순위 추천요소 radiobutton이 아무것도 클릭되지 않았을때 동작
                    Toast.makeText(getApplicationContext(),"모든 질문에 답해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    // 모든 질문에 답을 했을때 동작
                    Intent outintent = new Intent(Register2Activity.this, RegisterActivity.class);
                    sweetness = (int) sweet.getRating(); // 현재 표시된 별값
                    bitterness = (int) bitter.getRating(); // (이하동문)

                    switch(selectedDegreeId) {
                        case R.id.rdoDegree10:
                            degree = "10 이하";
                            break;
                        case R.id.rdoDegree20:
                            degree = "10~20";
                            break;
                        case R.id.rdoDegree30:
                            degree = "20~30";
                            break;
                        case R.id.rdoDegree40:
                            degree = "30~40";
                            break;
                        case R.id.rdoDegree50:
                            degree = "40~50";
                            break;
                    }

                    switch(selectedPriceId) {
                        case R.id.rdoPrice5k:
                            price = "1만원 이하";
                            break;
                        case R.id.rdoPrice25k:
                            price = "1~5만원";
                            break;
                        case R.id.rdoPrice75k:
                            price = "5~10만원";
                            break;
                        case R.id.rdoPrice100k:
                            price = "10만원 이상";
                            break;
                    }

                    switch(selectedCarbonId) {
                        case R.id.rdoCarbon:
                            carbonated = true;
                            break;
                        case R.id.rdoNocarbon:
                            carbonated = false;
                            break;
                    }

                    switch(selectedRecom1Id) {
                        case R.id.recom1Degree:
                            recom1st = "도수";
                            break;
                        case R.id.recom1Price:
                            recom1st = "가격";
                            break;
                        case R.id.recom1Sweet:
                            recom1st = "단맛";
                            break;
                        case R.id.recom1Bitter:
                            recom1st = "쓴맛";
                            break;
                        case R.id.recom1Carbon:
                            recom1st = "탄산 여부";
                            break;
                    }

                    switch(selectedRecom2Id) {
                        case R.id.recom2Degree:
                            recom2nd = "도수";
                            break;
                        case R.id.recom2Price:
                            recom2nd = "가격";
                            break;
                        case R.id.recom2Sweet:
                            recom2nd = "단맛";
                            break;
                        case R.id.recom2Bitter:
                            recom2nd = "쓴맛";
                            break;
                        case R.id.recom2Carbon:
                            recom2nd = "탄산 여부";
                            break;
                    }

                    switch(selectedRecom3Id) {
                        case R.id.recom3Degree:
                            recom3rd = "도수";
                            break;
                        case R.id.recom3Price:
                            recom3rd = "가격";
                            break;
                        case R.id.recom3Sweet:
                            recom3rd = "단맛";
                            break;
                        case R.id.recom3Bitter:
                            recom3rd = "쓴맛";
                            break;
                        case R.id.recom3Carbon:
                            recom3rd = "탄산 여부";
                            break;
                    }
                    if(recom1st.equals(recom2nd) || recom2nd.equals(recom3rd) || recom1st.equals(recom3rd)) {
                        Toast.makeText(getApplicationContext(), "오류! 1~3순위를 각각 다르게 설정해주세요!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //현재 표시된 단맛, 쓴맛의 별수를 반환
                        outintent.putExtra("sweetness", sweetness);
                        outintent.putExtra("bitterness", bitterness);
                        outintent.putExtra("degree", degree);
                        outintent.putExtra("price", price);
                        outintent.putExtra("carbonated", carbonated);
                        outintent.putExtra("recom1st",recom1st);
                        outintent.putExtra("recom2nd",recom2nd);
                        outintent.putExtra("recom3rd",recom3rd);
                        setResult(RESULT_OK, outintent);
                        finish();
                    }
                }
            }
        });

    }
}