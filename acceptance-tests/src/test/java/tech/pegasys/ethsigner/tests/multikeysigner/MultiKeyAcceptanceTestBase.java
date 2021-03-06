/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.ethsigner.tests.multikeysigner;

import static org.assertj.core.api.AssertionsForClassTypes.fail;

import tech.pegasys.ethsigner.tests.dsl.node.NodeConfiguration;
import tech.pegasys.ethsigner.tests.dsl.node.NodeConfigurationBuilder;
import tech.pegasys.ethsigner.tests.dsl.node.NodePorts;
import tech.pegasys.ethsigner.tests.dsl.signer.Signer;
import tech.pegasys.ethsigner.tests.dsl.signer.SignerConfiguration;
import tech.pegasys.ethsigner.tests.dsl.signer.SignerConfigurationBuilder;
import tech.pegasys.ethsigner.tests.dsl.utils.HashicorpVault;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;

public class MultiKeyAcceptanceTestBase {

  protected Signer ethSigner;

  @AfterEach
  public void cleanUp() {
    if (ethSigner != null) {
      ethSigner.shutdown();
      ethSigner = null;
    }
  }

  void setup(final Path tomlDirectory) {
    final SignerConfiguration signerConfig =
        new SignerConfigurationBuilder().withMultiKeySignerDirectory(tomlDirectory).build();
    final NodeConfiguration nodeConfig = new NodeConfigurationBuilder().build();

    ethSigner = new Signer(signerConfig, nodeConfig, new NodePorts(1, 2));
    ethSigner.start();
    ethSigner.awaitStartupCompletion();
  }

  public void createAzureTomlFileAt(
      final Path tomlPath, final String clientId, final String clientSecret) {
    try {

      final FileWriter writer = new FileWriter(tomlPath.toFile(), StandardCharsets.UTF_8);
      writer.write("[signing]\n");
      writer
          .append("type = \"azure-signer\"\n")
          .append("key-vault-name = \"ethsignertestkey\"\n")
          .append("key-name = \"TestKey\"\n")
          .append("key-version = \"7c01fe58d68148bba5824ce418241092\"\n")
          .append("client-id = \"" + clientId + "\"\n")
          .append("client-secret = \"" + clientSecret + "\"\n");
      writer.close();
    } catch (final IOException e) {
      fail("Unable to create Azure TOML file.");
    }
  }

  public void createFileBasedTomlFileAt(
      final Path tomlPath, final String keyPath, final String passwordPath) {
    try {
      final FileWriter writer = new FileWriter(tomlPath.toFile(), StandardCharsets.UTF_8);
      writer.write("[signing]\n");
      writer
          .append("type = \"file-based-signer\"\n")
          .append("key-file = \"" + keyPath + "\"\n")
          .append("password-file = \"" + passwordPath + "\"\n");
      writer.close();
    } catch (final IOException e) {
      fail("Unable to create Azure TOML file.");
    }
  }

  public void createHashicorpTomlFileAt(
      final Path tomlPath,
      final String keyPath,
      final String authFile,
      final HashicorpVault hashicorpVault) {
    {
      try {

        final FileWriter writer = new FileWriter(tomlPath.toFile(), StandardCharsets.UTF_8);
        writer.write("[signing]\n");
        writer
            .append("type = \"hashicorp-signer\"\n")
            .append("signing-key-path = \"" + keyPath + "\"\n")
            .append("host = \"" + hashicorpVault.getIpAddress() + "\"\n")
            .append("port = " + hashicorpVault.getPort() + "\n")
            .append("auth-file  = \"" + authFile + "\"\n")
            .append("timeout = 500\n");

        writer.close();
      } catch (final IOException e) {
        fail("Unable to create Hashicorp TOML file.");
      }
    }
  }
}
