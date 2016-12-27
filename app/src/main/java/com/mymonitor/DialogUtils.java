package com.mymonitor;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName: DialogUtils
 * @author: 张京伟
 * @date: 2016/11/29 17:29
 * @Description: 弹出对话框工具类
 * @version: 1.0
 */
public class DialogUtils {

    public interface OnDialogCallback{
        void dialogCallback(String dialogStr);
    }

    //居中弹出一个可以编写的Dialog
    //点击确定按钮
    public static void dialogCenterEditText(Context context, final OnDialogCallback onDialogCallback) {
        final CustomDialog customDialog = new CustomDialog(context, R.layout.popupwindow_add_hy, R.style.Theme_dialog);
        final EditText tv_desc = (EditText) customDialog.findViewById(R.id.tv_desc);
        final TextView tv_desc_num = (TextView) customDialog.findViewById(R.id.tv_desc_num);
        tv_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_desc_num.setText(s.length() + "/200");
            }
        });

        customDialog.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDialogCallback != null)
                    onDialogCallback.dialogCallback(tv_desc.getText().toString().trim());
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }
}
