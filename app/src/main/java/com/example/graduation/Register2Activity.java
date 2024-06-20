package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
    RadioGroup rgroupDegree, rgroupPrice, rgroupCarbonated;
    int sweetness, bitterness;
    String degree, price;
    Boolean carbonated;
    RatingBar sweet,bitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        rgroupDegree = findViewById(R.id.rgroupDegree); // 도수 radiogroup
        rgroupPrice = findViewById(R.id.rgroupPrice);   // 가격 radiogroup
        rgroupCarbonated = findViewById(R.id.rgroupCarbonated); // 탄산 radiogroup
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

                if (selectedDegreeId == -1 || selectedPriceId == -1 || selectedCarbonId == -1) {
                    // 도수, 가격, 탄산여부 radiobutton이 아무것도 클릭되지 않았을때 동작
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
                    //현재 표시된 단맛, 쓴맛의 별수를 반환
                    outintent.putExtra("sweetness", sweetness);
                    outintent.putExtra("bitterness", bitterness);
                    outintent.putExtra("degree", degree);
                    outintent.putExtra("price", price);
                    outintent.putExtra("carbonated", carbonated);
                    setResult(Activity.RESULT_OK, outintent);
                    finish();

                }
            }
        });

    }
}