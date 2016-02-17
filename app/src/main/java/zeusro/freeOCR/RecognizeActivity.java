package zeusro.freeOCR;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import zeusro.freeOCR.helper.ImageHelper;


public class RecognizeActivity extends AppCompatActivity {

    static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
//    private Button mButtonSelectImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private TextView mEditText;

    private VisionServiceClient client;

    private ImageView selectedImage;

    static final String bundlekey1 = "BitmapUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recognize);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }
//        mEditText = (EditText) findViewById(R.id.editTextResult);
        mEditText = (TextView) findViewById(R.id.editTextResult);
        selectedImage = (ImageView) findViewById(R.id.selectedImage);

        Intent requestIntent = getIntent();
        if (requestIntent != null && requestIntent.getStringExtra(bundlekey1) != null) {
            Log.d(this.getClass().getSimpleName(), String.valueOf(requestIntent.getStringExtra(bundlekey1)));
            mImageUri = Uri.parse(requestIntent.getStringExtra(bundlekey1));
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(mImageUri, getContentResolver());
            Log.d(this.getClass().getSimpleName(), String.valueOf(mBitmap.getByteCount() / 1024 / 1024) + "Mb");
            if (mBitmap != null) {
                ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                imageView.setImageBitmap(mBitmap);
                // Add detection log.
                Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth() + "x" + mBitmap.getHeight());
                doRecognize();
            }
        } else {
            Toast.makeText(RecognizeActivity.this, "宝宝有事了", Toast.LENGTH_SHORT).show();
        }
        setOnClickListener();
//        mButtonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
    }


    protected void setOnClickListener() {
        if (selectedImage != null) {
            selectedImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(RecognizeActivity.this, "shit2", Toast.LENGTH_SHORT).show();
                    return true;
                }

            });

            //全屏预览
            selectedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecognizeActivity.this, "shit", Toast.LENGTH_SHORT).show();
                    if (mImageUri != null) {

                        Intent loadImageIntent = new Intent(RecognizeActivity.this, ImageLoaderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(ImageLoaderActivity.bundleKey_ImageUrl, String.valueOf(mImageUri.toString()));
                        loadImageIntent.putExtras(bundle);
                        startActivity(loadImageIntent);

//                        LayoutInflater inflater = LayoutInflater.from(RecognizeActivity.this);
//                        View view = (LinearLayout) inflater.inflate(R.layout.imageloader, null);
//                        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.my_image_view);
//                        draweeView.setImageURI(mImageUri);
//                        RecognizeActivity.this.addContentView(view, null);
//                        RecognizeActivity.this.setContentView(v);
                    }

                }
            });
        }

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(mEditText.getText());
                android.content.ClipboardManager myClipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(RecognizeActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();

            }
        });

        mEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(RecognizeActivity.this, "caonima", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        mEditText.setText("");

        Intent intent;
        intent = new Intent(RecognizeActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AnalyzeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        doRecognize();
                    }
                }
                break;
            default:
                break;
        }
    }


    public void doRecognize() {
//        mButtonSelectImage.setEnabled(false);
        mEditText.setText("分析中,请不要断网...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: 2016/2/17 保存数据


    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (com.microsoft.projectoxford.vision.contract.Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }

                mEditText.setText(result);
            }
//            mButtonSelectImage.setEnabled(true);
        }
    }
}
