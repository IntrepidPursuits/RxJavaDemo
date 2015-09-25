package intrepid.io.rxjavademo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import intrepid.io.rxjavademo.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ViewBindingActivity extends AppCompatActivity {

    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_binding);
        ButterKnife.bind(this);

        RxTextView.textChanges(editText)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        textView.setText(charSequence);
                    }
                });
    }
}
