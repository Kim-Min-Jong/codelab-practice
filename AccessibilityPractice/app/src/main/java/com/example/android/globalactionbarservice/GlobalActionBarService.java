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

        // 전원 버튼 기능 추가
        configurePowerButton();
        // 볼륨 버튼 기능 추가
        configureVolumeButton();
        // 스크롤 버튼 기능 추가
        configureScrollButton();
    }

    // 전원 버튼 구성
    private void configurePowerButton() {
        Button powerButton = (Button) mLayout.findViewById(R.id.power);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전원 대화상자를 사용자에게 표시
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            }
        });
    }

    // 볼륨 버튼 구성
    private void configureVolumeButton() {
        Button volumeUpButton = (Button) mLayout.findViewById(R.id.volume_up);
        volumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                );
            }
        });
    }

    // 스크롤 가능한 노드를 찾음
    private AccessibilityNodeInfo findScrollableNode(AccessibilityNodeInfo root) {
        // 접근성 서비스는 화면 실제뷰에 액세스 불가능
        // AccessibilityNodeInfo로 구성된 트리형태를 반영하는 것 뿐
        // 정보(뷰의 위치, 메타데이터, 작업) 등을 찾아내기 위해 트리를 순회하는 작업을 실행
        // 스크롤 가능한 노드를 찾을 때 까지
        Deque<AccessibilityNodeInfo> deque = new ArrayDeque<>();
        deque.add(root);

        while(!deque.isEmpty()) {
            AccessibilityNodeInfo node = deque.removeFirst();

            if (node.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                return node;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                deque.addLast(node.getChild(i));
            }
        }
        return null;
    }

    // 사용자 대신 스크롤 작업을 실행
    private void configureScrollButton() {
        Button scrollButton = (Button) mLayout.findViewById(R.id.scroll);
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
                // 스크롤 가능한 노드가 있으면 스크롤을 실행
                if (scrollable != null) {
                    scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
                }
            }
        });
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


    }

    @Override
    public void onInterrupt() {

    }
}
