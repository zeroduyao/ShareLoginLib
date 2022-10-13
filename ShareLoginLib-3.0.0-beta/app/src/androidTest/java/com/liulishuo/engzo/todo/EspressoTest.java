package com.liulishuo.engzo.todo;


import com.liulishuo.demo.R;
import com.liulishuo.engzo.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        onView(withId(R.id.user_info_tv)).perform(click());
        
        onView(allOf(withId(R.id.user_info_tv), isDisplayed()))
                .check(matches(notNullValue()));

        Activity mainActivity = rule.getActivity();


        // test toast
//        onView(withText("用户名不能为空")).inRoot(withDecorView(not(mainActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    /*@Test
    public void testPasswordNotEmpty() {

        //注入用户名
        onView(withId(R.id.login_username)).perform(click(), clearText(), typeText("1234567"), closeSoftKeyboard());

        //点击登录按钮
        onView(withId(R.id.login_login_btn)).perform(click());
        //弹窗提示
        onView(withText("密码不能为空")).inRoot(withDecorView(not(mainActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin () {
        //注入用户名
        onView(withId(R.id.login_username)).perform(typeText("1234567"), closeSoftKeyboard());
        //注入密码
        onView(withId(R.id.login_password)).perform(typeText("123456"), closeSoftKeyboard());
        //点击登录按钮
        onView(withId(R.id.login_login_btn)).perform(click());
        //弹窗提示 登录成功
        onView(withText("登录成功")).inRoot(withDecorView(not(mainActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }}*/


}
