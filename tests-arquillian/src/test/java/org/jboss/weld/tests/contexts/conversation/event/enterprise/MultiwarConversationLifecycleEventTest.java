/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.contexts.conversation.event.enterprise;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import jakarta.servlet.http.HttpServletResponse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.ActionSequence;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

@Category(Integration.class)
@RunWith(Arquillian.class)
public class MultiwarConversationLifecycleEventTest {

    @Deployment(testable = false)
    public static Archive<?> getDeployment() {
        WebArchive war1 = ShrinkWrap.create(WebArchive.class).addClasses(TestServlet.class, ConversationBean.class, ObserverWeb1.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        WebArchive war2 = ShrinkWrap.create(WebArchive.class).addClass(ObserverWeb2.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        JavaArchive shared = ShrinkWrap.create(JavaArchive.class, "shared.jar").addClass(ActionSequence.class);
        return ShrinkWrap.create(EnterpriseArchive.class, Utils.getDeploymentNameAsHash(MultiwarConversationLifecycleEventTest.class, Utils.ARCHIVE_TYPE.EAR))
                .addAsModules(war1, war2).addAsLibrary(shared);
    }

    @ArquillianResource(TestServlet.class)
    private URL url;

    @Test
    public void testConversationContextEvents() throws Exception {
        WebClient client = new WebClient();
        Page page = client.getPage(getPath("begin"));
        assertEquals(page.getWebResponse().getStatusCode(), HttpServletResponse.SC_OK);
        String cid = page.getWebResponse().getContentAsString().trim();

        page = client.getPage(getPath("end" + "?cid=" + cid));
        assertEquals(page.getWebResponse().getStatusCode(), HttpServletResponse.SC_OK);

        page = client.getPage(getPath("status"));
        assertEquals(page.getWebResponse().getStatusCode(), HttpServletResponse.SC_OK);
        assertEquals(page.getWebResponse().getContentAsString().trim(), ObserverWeb1.class.getName());
    }

    protected String getPath(String path) {
        String base = url.toString();
        return base.endsWith("/") ? base + path : base + "/" + path;
    }
}
