package com.hitsujun.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import com.hitsujun.core.Hitsujun;

public class HitsujunActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new Hitsujun());
  }
}
