package kr.co.ezenac.cjy.teamproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ezenac.cjy.teamproject.adapter.Room_adapter;
import kr.co.ezenac.cjy.teamproject.model.Img;
import kr.co.ezenac.cjy.teamproject.model.Member;
import kr.co.ezenac.cjy.teamproject.model.Room;
import kr.co.ezenac.cjy.teamproject.retrofit.RetrofitService;
import kr.co.ezenac.cjy.teamproject.singletone.LoginInfo;
import kr.co.ezenac.cjy.teamproject.singletone.RoomInfo;
import kr.co.ezenac.cjy.teamproject.util.RealPathUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomActivity extends AppCompatActivity {
    Room_adapter roomAdapter;
    @BindView(R.id.grid_room_gv) GridView grid_room_gv;
    @BindView(R.id.img_room_Back) ImageView img_room_Back;
    @BindView(R.id.img_roomImg) ImageView img_roomImg;
    @BindView(R.id.text_room_name) TextView text_room_name;
    @BindView(R.id.in_room_img_add) Button in_room_img_add;
    @BindView(R.id.img_room_home) ImageView img_room_home;
    @BindView(R.id.img_room_search) ImageView img_room_search;
    @BindView(R.id.img_room_input) ImageView img_room_input;
    @BindView(R.id.img_room_option) ImageView img_room_option;
    @BindView(R.id.linearLayout_room) LinearLayout linearLayout_room;


    Integer room_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        room_id = intent.getIntExtra("room_id", 0);
        String room_name = intent.getStringExtra("room_name");
        String room_img = intent.getStringExtra("room_img");


        Log.d("room", "room_info : " + room_name + " / " + room_img +"/"+ room_id);
        text_room_name.setText(room_name);
        Glide.with(RoomActivity.this).load(room_img).centerCrop().
                into(img_roomImg);
        callImgInfo(room_id);
    }

    @OnClick(R.id.img_roomImg)
    public void onClickRoomImg(View view){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select picture"), 0);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        TedPermission.with(RoomActivity.this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == 0){
                File file = new File(RealPathUtil.getRealPath(RoomActivity.this, data.getData()));
                if (file.exists()){
                    Log.d("fff", "exist");
                }
                MultipartBody.Part filePart =
                        MultipartBody.Part.createFormData("file", file.getName(),
                                RequestBody.create(MediaType.parse("image/*"),file));

                RequestBody roomId =
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(room_id));

                Call<Room> observ = RetrofitService.getInstance().getRetrofitRequest().updateRoom(filePart, roomId);
                observ.enqueue(new Callback<Room>() {
                    @Override
                    public void onResponse(Call<Room> call, Response<Room> response) {
                        if (response.isSuccessful()){
                            Room room = response.body();
                            RoomInfo.getInstance().setRoom(room);
                            Log.d("yyy", room.toString());

                            String updateImg = RoomInfo.getInstance().getRoom().getRoom_img();
                            Glide.with(RoomActivity.this).load(updateImg).centerCrop().
                                    into(img_roomImg);
                            Log.d("profile", "success : " + room.toString());


                        } else {
                            Log.d("profile", "fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<Room> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    @OnClick(R.id.in_room_img_add)
    public void onClick_in_room_img_add(View view){
        Intent intent = new Intent(RoomActivity.this, upload_imgActivity.class);
        intent.putExtra("room_id", room_id);
        Log.d("kkk","room_id" + room_id);
        startActivity(intent);
    }

    public void callImgInfo(Integer room_id){
        Call<ArrayList<Img>> observ = RetrofitService.getInstance().getRetrofitRequest().
                callRoomImg(room_id);
        observ.enqueue(new Callback<ArrayList<Img>>() {
            @Override
            public void onResponse(Call<ArrayList<Img>> call, Response<ArrayList<Img>> response) {
                if (response.isSuccessful()) {
                    final ArrayList<Img> items = response.body();

                    Log.d("uuu", items.toString());


                    roomAdapter = new Room_adapter(items, RoomActivity.this);
                    grid_room_gv.setAdapter(roomAdapter);
                    grid_room_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(RoomActivity.this, DetailActivity.class);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.d("uuu", "1");
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Img>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    @OnClick(R.id.img_room_input)
    public void onClickChange(View view){
        initDialog();
    }

    private  void  initDialog(){
        ChooseDialog dialog = new ChooseDialog(this, new ChooseDialog.ChooseListener() {
            @Override
            public void choosePhoto() {

                Intent intent = new Intent(RoomActivity.this,upload_Activity.class);
                startActivity(intent);
                Log.d("bjh","re: " + 3);
            }

            @Override
            public void chooseCamer() {


                Intent intent = new Intent(RoomActivity.this,upload_btn_photo_activity.class);

                startActivity(intent);
                Log.d("bjh","re: " + 55);
            }
        });
        dialog.show();
    }
    @OnClick(R.id.img_room_home)
    public void onReturnHome(View view) {
        Intent intent = new Intent(RoomActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.img_room_search)
    public void onReturnSearch(View view) {
        Intent intent = new Intent(RoomActivity.this, SearchActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.img_room_option)
    public void onReturnOption(View view) {
        Intent intent = new Intent(RoomActivity.this, MainActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.linearLayout_room)
    public void onClickMain(View view){

    }
}
