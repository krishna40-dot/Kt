package com.taxiappclone.common.custom;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import com.taxiappclone.common.R;

public class CustomProgressDialog
  extends Dialog
{
  public Activity c;
  public Dialog d;
  public Button no;
  public int theme;
  public Button yes;
  
  public CustomProgressDialog(Activity paramActivity)
  {
    super(paramActivity);
    theme = R.style.CustomDialogTheme;
    c = paramActivity;
    getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
  }

  public void onCreate(Bundle paramBundle)
  {
    Activity localActivity = c;
    int i = theme;
    localActivity.setTheme(i);
    super.onCreate(paramBundle);
    boolean bool = requestWindowFeature(1);
    setContentView(R.layout.dialog_custom_progress);
  }
}
