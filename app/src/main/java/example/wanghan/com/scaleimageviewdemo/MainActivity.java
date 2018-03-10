package example.wanghan.com.scaleimageviewdemo;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private ScaleImageView scaleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scaleImageView=findViewById(R.id.image);
        Glide.with(this)
             .load(R.drawable.ic_image)

                .into(scaleImageView);

    }
}
