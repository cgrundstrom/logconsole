package org.logconsole;

/*
 * Copyright (c) 2011, Joshua Kaplan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *    disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *  - Neither the name of matlabcontrol nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

/**
 * On OS X, sets the demo to have a dock icon and a name in the menu bar. On other operating systems does nothing.
 *
 * @author <a href="mailto:nonother@gmail.com">Joshua Kaplan</a>
 */
public class PlatformAppearance {
    public static void apply(String icon) {
        String osName = System.getProperties().getProperty("os.name");
        if (osName.startsWith("Mac OS X")) {
            //Set the System menu bar
            // Do this with -Xdock:name="Wave Machine"
//            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wave Machine");

            //Set the dock icon using reflection so that no OS X specific classes are referenced - which would be a
            //problem on other platforms
            try {
                Image dockIcon = ImageIO.read(PlatformAppearance.class.getResource(icon));

//                com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
//                application.setDockIconImage(dockIcon);

                Class<?> appClass = Class.forName("com.apple.eawt.Application");
                Method getAppMethod = appClass.getMethod("getApplication");
                Object appInstance = getAppMethod.invoke(null);
                Method dockMethod = appInstance.getClass().getMethod("setDockIconImage", java.awt.Image.class);
                dockMethod.invoke(appInstance, dockIcon);
            }
            //If this does not work, it does not actually matter
            catch (Exception ignored) {
            }
        }
        else if (osName.equals("Linux")) {
            try {
                System.setProperty("awt.useSystemAAFontSettings","on"); // Turn on anti-aliasing
                UIManager.LookAndFeelInfo info[] = UIManager.getInstalledLookAndFeels();
                String className = null;
                for (int i = 0; i < info.length; i++) {
                    String name = info[i].getName();
                    //System.out.println(name);
                    if (name.equals("Nimbus"))
                        className = info[i].getClassName();
                    else if (name.equals("GTK+") && className == null)
                        className = info[i].getClassName();
                }
                if (className != null)
                    UIManager.setLookAndFeel(className);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
