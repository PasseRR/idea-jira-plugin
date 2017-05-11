package com.gome.idea.plugins.jira;

import com.intellij.notification.Notification;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 通知timer
 * @author xiehai1
 * @date 2017/05/11 14:32
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraNotificationTimer extends Timer {
    /**
     * Creates a {@code Timer} and initializes both the initial delay and
     * between-event delay to {@code delay} milliseconds. If {@code delay}
     * is less than or equal to zero, the timer fires as soon as it
     * is started. If <code>listener</code> is not <code>null</code>,
     * it's registered as an action listener on the timer.
     *
     * @param delay    milliseconds for the initial and between-event delay
     * @param listener an initial listener; can be <code>null</code>
     * @see #addActionListener
     * @see #setInitialDelay
     * @see #setRepeats
     */
    public GJiraNotificationTimer(int delay, ActionListener listener) {
        super(delay, listener);
    }

    public GJiraNotificationTimer(int delay, final Notification notification){
        this(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notification.expire();
            }
        });
        this.setRepeats(false);
    }

    public GJiraNotificationTimer(final Notification notification){
        this(2000, notification);
    }
}
