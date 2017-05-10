package com.gome.idea.plugins.jira.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Gjira settings Configurable
 * @author xiehai1
 * @date 2017/05/10 10:49
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraConfigurable implements SearchableConfigurable {
    public GJiraConfigurable() {
    }

    @Nls
    @Override
    public String getDisplayName() {
        return this.getId();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return this.getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return GJiraForm.me().getRootComponent();
    }

    @Override
    public boolean isModified() {
        return GJiraForm.me().isModify();
    }

    @Override
    public void apply() throws ConfigurationException {
        GJiraSettings.me().setJiraUrl(GJiraForm.me().getJiraUrl());
        GJiraSettings.me().setUsername(GJiraForm.me().getUsername());
        GJiraSettings.me().setPassword(GJiraForm.me().getPassword());
    }

    @Override
    public void reset() {
        GJiraForm.me().reset();
    }

    @Override
    public void disposeUIResources() {

    }

    @NotNull
    @Override
    public String getId() {
        return "Gjira";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }
}
