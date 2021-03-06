package kr.co.ezenac.cjy.teamproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ezenac.cjy.teamproject.Fragment.RoomFragment;
import kr.co.ezenac.cjy.teamproject.adapter.Profile_adapter;
import kr.co.ezenac.cjy.teamproject.adapter.ViewPagerAdapter;
import kr.co.ezenac.cjy.teamproject.customview.BackPressCloseHandler;
import kr.co.ezenac.cjy.teamproject.model.Main;
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

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.img_mainProfile) ImageView img_mainProfile;
    @BindView(R.id.text_mainId) TextView text_mainId;
    @BindView(R.id.img_home) ImageView img_home;
    @BindView(R.id.img_search) ImageView img_search;
    @BindView(R.id.img_input) ImageView img_input;
    @BindView(R.id.linearLayout_main) LinearLayout linearLayout_main;
    @BindView(R.id.mainpage_id) RelativeLayout mainpage_id;
    ViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.viewpager_1) ViewPager viewpager_1;
    @BindView(R.id.btn_room) ImageView btn_room;
    @BindView(R.id.btn_collection) ImageView btn_collection;
    @BindView(R.id.btn_logout) ImageView btn_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("jjj","bbb"+  LoginInfo.getInstance().getMember().getId());
        Integer tmpId = LoginInfo.getInstance().getMember().getId();
        String tmpMember_id = LoginInfo.getInstance().getMember().getLogin_id().toString();
        String tmpMember_img = LoginInfo.getInstance().getMember().getMember_img();

        //★
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager_1.setAdapter(viewPagerAdapter);

        Glide.with(MainActivity.this).load(tmpMember_img).centerCrop().
                into(img_mainProfile);
        text_mainId.setText(tmpMember_id);

        Log.d("img", "img : " + LoginInfo.getInstance().getMember().getMember_img());
    }

    public void refreshList(){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager_1.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onResume() {
        refreshList();
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //★
    @OnClick(R.id.btn_room)
    public void click_btn_room(View view){
        Log.d("joseph","aa");
        view();
        btn_room.setBackgroundResource(R.drawable.list_2);
        btn_collection.setBackgroundResource(R.drawable.star);
        }


    //★
    @OnClick(R.id.btn_collection)
    public void click_btn_collection(View view){
        Log.d("joseph","bb");
        view2();
        btn_collection.setBackgroundResource(R.drawable.starr);
        btn_room.setBackgroundResource(R.drawable.list_unclick);
    }
    //★
    public void view(){
        RoomFragment roomFragment = (RoomFragment) viewPagerAdapter.getFragmentIndex(0);
        viewpager_1.setCurrentItem(0);
    }
    //★
    public void view2(){
        viewpager_1.setCurrentItem(1);
    }

    @OnClick(R.id.img_mainProfile)
    public void onClickProfileImg(View view){
        PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"),0);
            }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

        TedPermission.with(MainActivity.this)
            .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == 0){
                File file = new File(RealPathUtil.getRealPath(MainActivity.this, data.getData()));
                if (file.exists()){
                    Log.d("fff", "exist");
                }
                MultipartBody.Part filePart =
                        MultipartBody.Part.createFormData("file", file.getName(),
                                RequestBody.create(MediaType.parse("image/*"),file));
                Integer id = LoginInfo.getInstance().getMember().getId();

                RequestBody loginIdBody =
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));

                Call<Member> observ = RetrofitService.getInstance().getRetrofitRequest().updateProfile(filePart, loginIdBody);
                observ.enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        if (response.isSuccessful()){
                            Member member = response.body();
                            LoginInfo.getInstance().setMember(member);
                            Log.d("yyy", member.toString());
                            Log.d("yyy", LoginInfo.getInstance().getMember().getMember_img());

                            String updateImg = LoginInfo.getInstance().getMember().getMember_img();
                            Glide.with(MainActivity.this).load(updateImg).centerCrop().
                                    into(img_mainProfile);
                            Log.d("profile", "success : " + member.toString());


                        } else {
                            Log.d("profile", "fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {

                    }
                });
            }
        }
    }

    @OnClick(R.id.linearLayout_main)
    public void onClickMain(View view){
    }

    @OnClick(R.id.img_mainBack)
    public void onClickImgBack(View view){
        finish();
    }


    @OnClick(R.id.img_input)
    public void onClickChange(View view){
        initDialog();
    }

    private  void  initDialog(){
        ChooseDialog dialog = new ChooseDialog(this, new ChooseDialog.ChooseListener() {
            @Override
            public void choosePhoto() {

                Intent intent = new Intent(MainActivity.this,upload_Activity.class);
                startActivity(intent);
                Log.d("bjh","re: " + 3);
            }

            @Override
            public void chooseCamer() {


                Intent intent = new Intent(MainActivity.this,upload_btn_photo_activity.class);

                startActivity(intent);
                Log.d("bjh","re: " + 55);
            }
        });
        dialog.show();
    }
    @OnClick(R.id.img_home)
    public void onReturnHome(View view) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    @OnClick(R.id.img_search)
    public void onReturnSearch(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mainpage_id)
    public void onReturnPresentPage(View view) {
        if(viewpager_1.getCurrentItem()==0){
            RoomFragment roomFragment = (RoomFragment) viewPagerAdapter.getFragmentIndex(0);//●굳굳굳굳
            roomFragment.getProfileAdapter().setMode(0);
            roomFragment.getProfileAdapter().notifyDataSetChanged();
        }

    }
    @OnClick(R.id.btn_logout)
    public void onClickLogout(View view){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("경고");
        alertDialog.setMessage("로그아웃 하시겠습니까?");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        });
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
}
