package com.gome.idea.plugins.jira.schedule;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
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
            timer.schedule(new NotificationTimerTask(), 30 * 1000); // 如果过了第一次校验时间点 延迟30秒检查
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        timer.schedule(new NotificationTimerTask(), calendar.getTime(), 1000 * 60 * 60 * 24);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 17); // 第二次17点
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (hour > 16) {
            timer.schedule(new NotificationTimerTask(), 30 * 1000); // 如果过了第二次校验时间点 延迟30秒检查
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        timer.schedule(new NotificationTimerTask(), calendar.getTime(), 1000 * 60 * 60 * 24);
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "TimerApplication";
    }

    /**
     * 提示定时任务
     */
    private class NotificationTimerTask extends TimerTask {
        @Override
        public void run() {
            this.notification();
        }

        private void notification() {
            // 没有配置jira 不提示
            if (!JiraHttpUtil.login()) {
                return;
            }
            if (!JiraHttpUtil.isTodayLoged()) {
                final Project project = ProjectManager.getInstance().getOpenProjects()[0];
                final Notification notification = new Notification(
                        "GJira",
                        "jira工作日志",
                        "您还未更新jira!<br/><a href=\"\">前往</a>",
                        NotificationType.INFORMATION,
                        new NotificationListener() {
                            @Override
                            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                    final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("GJira");
                                    if(!toolWindow.isActive()){
                                        toolWindow.activate(null, false);
                                    }
                                    notification.expire();
                                }
                            }
                        }
                );
                Notifications.Bus.notify(notification, project);
            }
        }
    }

}
