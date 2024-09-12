// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.android.globalactionbarservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlobalActionBarService extends AccessibilityService {
    // 작업 모음의 레이아웃을 저장할 변수
    FrameLayout mLayout;

    // 서비스가 연결될 떄의 콜백
    @Override
    protected void onServiceConnected() {
        // 래이아웃을 확장하고 작업 모음을 추가

        // wm 선언
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 레이아웃 초기화
        mLayout = new FrameLayout(this);

        // 레이아웃 파라미터 객체 선언
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        // 파라미터 조정

        // 별다른 권한 처리 없이 기존 컨텐츠 위에 접근성 레이아웃을 그릴 수 있음
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;

        // 레이아웃을 그릴 inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        // frameLayout에 action bar를 그림
        inflater.inflate(R.layout.action_bar, mLayout);

        wm.addView(mLayout, lp);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


    }

    @Override
    public void onInterrupt() {

    }
}
