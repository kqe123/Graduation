package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.graduation.ml.AutoModel6class;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class CaptureActivity extends AppCompatActivity {
    Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);


        Intent intent = getIntent();
        String imgName = intent.getStringExtra("fileName");

        try {
            String imgpath = getFilesDir().getPath() + "/" + imgName;  // 내부 저장소에 저장되어 있는 이미지 경로
            bm = BitmapFactory.decodeFile(imgpath);
            ImageView imageView = findViewById(R.id.capture_image);
            imageView.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
            System.out.println("------------입력 이미지 처리전 가로 :" +bm.getWidth());
            System.out.println("------------입력 이미지 처리전 세로 :" +bm.getHeight());
            Toast.makeText(getApplicationContext(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }

        Button setBtn = findViewById(R.id.btn_setting);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = null;
                try {
                    AutoModel6class model = AutoModel6class.newInstance(CaptureActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 150, 150, 3}, DataType.FLOAT32);
                    bm = Bitmap.createScaledBitmap(bm,150,150,true);
                    Bitmap bm2 = bm.copy(Bitmap.Config.ARGB_8888, true);
                    ByteBuffer inputBuffer = normalize(bm2);
                    inputFeature0.loadBuffer(inputBuffer);

                    // Runs model inference and gets result.
                    AutoModel6class.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    float[] confidences = outputFeature0.getFloatArray();
                    int maxPos = 0;
                    float maxConfidence = 0;
                    for(int i = 0 ; i < confidences.length; i++) {
                        if(confidences[i] > maxConfidence) {
                            maxConfidence = confidences[i];
                            maxPos = i;
                        }
                    }
                    
                    if(getMax(outputFeature0.getFloatArray()) == 0) { result = "참이슬";}
                    else if(getMax(outputFeature0.getFloatArray()) == 2) { result = "카스";}
                    else if(getMax(outputFeature0.getFloatArray()) == 4) { result = "조니 워커 블랙 레이블";}
                    else {result = "없음";}

                    if (!(result.equals("없음"))) {
                        Toast.makeText(getApplicationContext(), result + ", " +
                                        confidences[getMax(outputFeature0.getFloatArray())] * 100 + "%" ,
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "인식되는 술이 없습니다!" ,
                                Toast.LENGTH_SHORT).show();
                    }

                    // 모델 종료
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }
                if (result.equals("참이슬")) {
                    Intent intent = new Intent(CaptureActivity.this, AlcoholActivity.class);
                    intent.putExtra("name", result);
                    startActivity(intent);
                }
                else if (result.equals("카스")) {
                    Intent intent = new Intent(CaptureActivity.this, AlcoholActivity.class);
                    intent.putExtra("name", result);
                    startActivity(intent);
                }
                else if (result.equals("조니 워커 블랙 레이블")) {
                    Intent intent = new Intent(CaptureActivity.this, AlcoholActivity.class);
                    intent.putExtra("name", result);
                    startActivity(intent);
                }
                
            }
        });

        // 취소버튼
        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto(getApplicationContext(),imgName);
                finish(); // 현재 액티비티 파괴
            }
        });


    }

    int getMax(float[] arr){
        int max=0;
        for(int i=0; i<arr.length; i++) {
            if(arr[i] > arr[max]) max=i;
        }
        return max;
    }

    // Bitmap을 ByteBuffer로 변환하는 함수
    private ByteBuffer normalize(Bitmap bitmap) {
        int imageSize = 150 * 150;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[imageSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < 150; i++) {
            for (int j = 0; j < 150; j++) {
                int value = intValues[pixel++];

                // 픽셀의 RGB 값을 가져오기
                float red = ((value >> 16) & 0xFF) / 255.0f;
                float green = ((value >> 8) & 0xFF) / 255.0f;
                float blue = (value & 0xFF) / 255.0f;

                // ByteBuffer에 float 값으로 저장
                byteBuffer.putFloat(red);
                byteBuffer.putFloat(green);
                byteBuffer.putFloat(blue);
            }
        }
        byteBuffer.rewind();
        return byteBuffer;
    }

    public void deletePhoto(Context context, String filename) {
        // 내부 저장소 경로 가져오기
        File dir = context.getFilesDir();
        // 삭제할 파일 객체 생성
        File file = new File(dir, filename);
        // 파일 삭제
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("-----------------해당 파일이 정상적으로 삭제됬습니다.");
            }
        } else {
            System.out.println("File does not exist");
        }
    }



}