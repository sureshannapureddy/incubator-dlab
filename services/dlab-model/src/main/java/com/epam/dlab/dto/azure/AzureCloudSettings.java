/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.epam.dlab.dto.azure;

import com.epam.dlab.dto.base.CloudSettings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AzureCloudSettings extends CloudSettings {

    @JsonProperty("azure_region")
    private String azureRegion;
    @JsonProperty("azure_iam_user")
    private String azureIamUser;
    @JsonProperty("azure_vpc_name")
    private String azureVpcName;
    @JsonProperty("azure_subnet_name")
    private String azureSubnetName;
    @JsonProperty("azure_resource_group_name")
    private String azureResourceGroupName;
    @JsonProperty("azure_security_group_name")
    private String azureSecurityGroupName;
    @JsonProperty("conf_key_dir")
    protected String confKeyDir;
    @JsonProperty("conf_image_enabled")
    private String imageEnabled;
    @JsonProperty("conf_shared_image_enabled")
    private String sharedImageEnabled;
    @JsonProperty("conf_stepcerts_enabled")
    private String stepCertsEnabled;
    @JsonProperty("conf_stepcerts_root_ca")
    private String stepCertsRootCA;
    @JsonProperty("conf_stepcerts_kid")
    private String stepCertsKid;
    @JsonProperty("conf_stepcerts_kid_password")
    private String stepCertsKidPassword;
    @JsonProperty("conf_stepcerts_ca_url")
    private String stepCertsCAURL;
    @JsonProperty("keycloak_auth_server_url")
    private String keycloakAuthServerUrl;
    @JsonProperty("keycloak_realm_name")
    private String keycloakRealmName;
    @JsonProperty("keycloak_user")
    private String keycloakUser;
    @JsonProperty("keycloak_user_password")
    private String keycloakUserPassword;

    @Override
    @JsonIgnore
    public String getIamUser() {
        return azureIamUser;
    }
}
