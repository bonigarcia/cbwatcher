/*
 * (C) Copyright 2021 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.cbwatcher.test;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.By;

import io.github.bonigarcia.cbwatcher.test.parent.BrowserParentTest;
import io.github.bonigarcia.wdm.config.DriverManagerType;

/**
 * WebRTC test.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */
public class WebRtcTest extends BrowserParentTest {

    static final int TEST_TIME_SEC = 10;

    @ParameterizedTest
    @EnumSource(names = { "CHROME" })
    public void webRtcTest(DriverManagerType browserType) {
        driver.get(
                "https://webrtc.github.io/samples/src/content/peerconnection/pc1/");

        injectJavaScriptCode("var peerConnections = [];"
                + "var origPeerConnection = window.RTCPeerConnection;"
                + "window.RTCPeerConnection = function(pcConfig, pcConstraints) {"
                + "    var pc = new origPeerConnection(pcConfig, pcConstraints);"
                + "    peerConnections.push(pc);" + "    return pc;" + "};"
                + "window.RTCPeerConnection.prototype = origPeerConnection.prototype;");

        driver.findElement(By.id("startButton")).click();
        waitSeconds(1);

        driver.findElement(By.id("callButton")).click();
        waitSeconds(TEST_TIME_SEC);

        driver.findElement(By.id("hangupButton")).click();

        Object peerConnections = readJavaScriptVariable("peerConnections");

        assertThat(peerConnections).isNotNull();
    }

}
