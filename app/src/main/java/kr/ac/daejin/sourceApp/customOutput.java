package kr.ac.daejin.sourceApp;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class customOutput extends AppCompatActivity {

    Dialog customdialog;
    TextView dialog_msg;
    EditText[] cart;
    TextView[] cartName;
    source_class.SourceList sourceList;
    ArrayList<String> comp_list;

    OutputStream outputStream;
    int checkConnecting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_output_page);

        customdialog = new Dialog(this);
        customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customdialog.setContentView(R.layout.custom_dialog);
        dialog_msg = customdialog.findViewById(R.id.dialog_msg);


        comp_list = ((MainActivity) MainActivity.context_main).comp_list;
        checkConnecting = ((MainActivity) MainActivity.context_main).checkConnecting;
        sourceList = ((MainActivity) MainActivity.context_main).sourceList;
        outputStream = ((MainActivity) MainActivity.context_main).outputStream;

        cart = new EditText[6];
        Integer[] cartId = {
                R.id.cart1, R.id.cart2, R.id.cart3, R.id.cart4, R.id.cart5, R.id.cart6
        };

        cartName = new TextView[6];
        Integer[] cartNameId = {
                R.id.cart1Name, R.id.cart2Name, R.id.cart3Name, R.id.cart4Name, R.id.cart5Name, R.id.cart6Name
        };


        for (int i = 0; i < 6; i++) {
            cart[i] = findViewById(cartId[i]);
            cartName[i] = findViewById(cartNameId[i]);
        }

        Button customOutputSendBtn, saveSourceCompBtn;
        customOutputSendBtn = (Button) findViewById(R.id.customOutputSendBtn);
        saveSourceCompBtn = (Button) findViewById(R.id.saveSourceCompBtn);

        for (int i = 0; i < 6; i++) {
            if (sourceList.getCurrentSourceExist()[i] != 0) {
                cartName[i].setText(sourceList.getCurrentSourceList()[i].getName());
            } else {
                cartName[i].setText("X");
            }
        }

        customOutputSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_msg.setText("????????? ???????????????.");
                showDialog();
            }
        });

        // ?????? ?????? ?????? ??????
        saveSourceCompBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registCompDialog();
            }
        });



    }

    public void showDialog() {
//        dialog_msg.setText("????????? ?????? ???????????????????");
        customdialog.show();
        customdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // ?????? ??????

        // ????????? ??????
        Button noBtn = customdialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ????????? ?????? ??????

                customdialog.dismiss(); // ??????????????? ??????
            }
        });
        // ??? ??????
        customdialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ????????? ?????? ??????
                String sendStr = getOutputString(cart, sourceList);

                if (checkConnecting == 1) {
                    // ????????? ????????? ??????
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            byte[] buffer = new byte[1024];
                            try {
                                // ????????? ??????
                                buffer = sendStr.getBytes(StandardCharsets.UTF_8);
                                outputStream.write(buffer);
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                customdialog.dismiss(); // ??????????????? ??????
                            }
                        }
                    }).start();
                    Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                }

                customdialog.dismiss(); // ??????????????? ??????
            }
        });
    }

    @NonNull
    private String getOutputString(EditText[] cart, source_class.SourceList sourceList) {
        int num = 0;
        StringBuilder cartNum = new StringBuilder();
        StringBuilder weight = new StringBuilder();
        StringBuilder isLiquid = new StringBuilder();
        StringBuilder sendStr = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            if (cart[i] != null && sourceList.getCurrentSourceExist()[i] == 1) {
                // ???????????? ??????
                cartNum.append(i + 1).append(" ");

                // ?????????
                weight.append(cart[i].getText().toString()).append(" ");

                // ?????? ??????
                isLiquid.append(sourceList.getCurrentSourceList()[i].getIsLiquid()).append(" ");

                // ?????? ?????? ??????
                num++;
            }
        }
        sendStr.append(num).append(",").append(cartNum).append(",").append(weight).append(",").append(isLiquid);
        return sendStr.toString();
    }

    public void registCompDialog() {
        customdialog = new Dialog(this);
        customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customdialog.setContentView(R.layout.custom_dialog_textinput);
        EditText cartNumDialogInput = customdialog.findViewById(R.id.cartNumDialogInput);
        customdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // ?????? ??????

        dialog_msg = customdialog.findViewById(R.id.dialog_msg);
        dialog_msg.setText("?????? ??????");

        customdialog.show();


        // ????????? ??????
        Button noBtn = customdialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customdialog.dismiss(); // ??????????????? ??????
            }
        });
        // ??? ??????
        customdialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customdialog.dismiss(); // ??????????????? ??????

                String name = cartNumDialogInput.getText().toString();

                String sendStr = getOutputString(cart, sourceList);
                String nameInfo = name + " " + sendStr;
                comp_list.add(nameInfo);

            }
        });
    }
//
//    public void getNameDialog(int num) {
//        customdialog = new Dialog(getActivity());
//        customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        customdialog.setContentView(R.layout.custom_dialog);
//        dialog_msg = customdialog.findViewById(R.id.dialog_msg);
//        dialog_msg.setText("????????? ????????? ???????????? ?????????????????????????");
//        customdialog.show();
//        customdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // ?????? ??????
//
//
//        // ????????? ??????
//        Button noBtn = customdialog.findViewById(R.id.noBtn);
//        noBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // ????????? ?????? ??????
//                customdialog.dismiss(); // ??????????????? ??????
//            }
//        });
//        // ??? ??????
//        customdialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // ????????? ?????? ??????
//                sourceList.regist_current_source(num, position);
//                String str = num + 1 + "??? ??????????????? ?????????????????????.";
//                Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
//
//                // ?????????
//                Toast.makeText( getActivity().getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
//
//                customdialog.dismiss(); // ??????????????? ??????
//            }
//        });
//    }

}
