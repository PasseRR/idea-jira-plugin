package com.gome.idea.plugins.jira.schedule;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xiehai1
 * @date 2017/05/11 16:34
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class TimerApplication implements ApplicationComponent {
    private Timer timer = new Timer();

    public TimerApplication() {

    }

    @Override
    public void initComponent() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 10); // 第一次10点
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (hour > 9) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerApplication.this.notification();
            }
        }, calendar.getTime(), 1000 * 60 * 60 * 24);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 17); // 第二次17点
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (hour > 16) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerApplication.this.notification();
            }
        }, calendar.getTime(), 1000 * 60 * 60 * 24);
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "TimerApplication";
    }

    private void notification() {
        if (!JiraHttpUtil.isTodayLoged()) {
            final Notification notification = new Notification("GJiraNotify", "Gjira", "您还未更新jira!", NotificationType.WARNING);
            Notifications.Bus.notify(notification);
        }
    }
}
