/*
 * The MIT License
 *
 * Copyright (c) 2014 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.sshd;


import com.jcabi.ssh.SSH;
import com.jcabi.ssh.Shell;
import org.apache.commons.io.FileUtils;
import org.arquillian.cube.CubeController;
import org.hamcrest.core.Is;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// tag::test[]
@RunWith(Arquillian.class) //<1>
public class SshdTest {


    @Test
    public void testSshConnection() throws IOException { //<2>

        Shell shell = new SSH("192.168.99.100", 2222, "test", getPrivateKeyString()); //<3>
        String stdout = new Shell.Plain(shell).exec("echo 'Hello, world!'");

        assertThat(stdout.trim(), is("Hello, world!"));

    }
// end::test[]

    private File privateKey;
    private File privateKeyEnc;

    /**
     * Get plaintext Private Key File
     */
    private File getPrivateKey() {
        if (privateKey == null) {
            try {
                privateKey = File.createTempFile("ssh", "key");
                privateKey.deleteOnExit();
                FileUtils.copyURLToFile(SshdTest.class.getResource("/sshd/unsafe"), privateKey);
                Files.setPosixFilePermissions(privateKey.toPath(), EnumSet.of(OWNER_READ));
            } catch (IOException e) {
                throw new RuntimeException("Not able to get the plaintext SSH key file. Missing file, wrong file permissions?!");
            }
        }
        return privateKey;
    }

    /**
     * Get encrypted Private Key File
     */
    private File getEncryptedPrivateKey() {
        if (privateKeyEnc == null) {
            try {
                privateKeyEnc = File.createTempFile("ssh_enc", "key");
                privateKeyEnc.deleteOnExit();
                FileUtils.copyURLToFile(SshdTest.class.getResource("/sshd/unsafe_enc_key"), privateKeyEnc);
                Files.setPosixFilePermissions(privateKeyEnc.toPath(), EnumSet.of(OWNER_READ));
            } catch (IOException e) {
                throw new RuntimeException("Not able to get the encrypted SSH key file. Missing file, wrong file permissions?!");
            }
        }
        return privateKeyEnc;
    }

    private String getPrivateKeyString() {
        try {
            return new String(Files.readAllBytes(getPrivateKey().toPath()));
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
    }
    // tag::test[]
}
// end::test[]