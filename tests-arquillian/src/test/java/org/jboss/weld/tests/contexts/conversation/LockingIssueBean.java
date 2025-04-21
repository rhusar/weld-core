package org.jboss.weld.tests.contexts.conversation;

import org.jboss.weld.context.http.HttpConversationContext;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Conversation;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import java.io.Serializable;

@Model
@ConversationScoped
public class LockingIssueBean implements Serializable {

    @Inject
    Conversation conversation;

    @Inject
    HttpConversationContext conversationContext;

    private String name;

    @PostConstruct
    void init() {
        this.name = "Gavin";
    }

    public void dummy() {
        throw new NullPointerException();
    }

    public String start() {
        conversation.begin();
        name = "Pete";
        return "start";
    }

    public String getName() {
        return name;
    }

    public String getCid() {
        return conversation.getId();
    }

}
