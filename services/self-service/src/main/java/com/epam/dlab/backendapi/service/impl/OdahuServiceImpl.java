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

package com.epam.dlab.backendapi.service.impl;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.annotation.BudgetLimited;
import com.epam.dlab.backendapi.annotation.Project;
import com.epam.dlab.backendapi.dao.OdahuDAO;
import com.epam.dlab.backendapi.domain.OdahuActionDTO;
import com.epam.dlab.backendapi.domain.OdahuDTO;
import com.epam.dlab.backendapi.domain.ProjectDTO;
import com.epam.dlab.backendapi.domain.RequestId;
import com.epam.dlab.backendapi.service.EndpointService;
import com.epam.dlab.backendapi.service.OdahuService;
import com.epam.dlab.backendapi.service.ProjectService;
import com.epam.dlab.backendapi.util.RequestBuilder;
import com.epam.dlab.constants.ServiceConsts;
import com.epam.dlab.dto.UserInstanceStatus;
import com.epam.dlab.dto.base.odahu.OdahuResult;
import com.epam.dlab.exceptions.ResourceConflictException;
import com.epam.dlab.rest.client.RESTService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class OdahuServiceImpl implements OdahuService {

    private static final String CREATE_ODAHU_API = "infrastructure/odahu";
    private static final String START_ODAHU_API = "infrastructure/odahu/start";
    private static final String STOP_ODAHU_API = "infrastructure/odahu/stop";
    private static final String TERMINATE_ODAHU_API = "infrastructure/odahu/terminate";

    private final ProjectService projectService;
    private final EndpointService endpointService;
    private final OdahuDAO odahuDAO;
    private final RESTService provisioningService;
    private final RequestBuilder requestBuilder;
    private final RequestId requestId;

    @Inject
    public OdahuServiceImpl(ProjectService projectService, EndpointService endpointService, OdahuDAO odahuDAO,
                            @Named(ServiceConsts.PROVISIONING_SERVICE_NAME) RESTService provisioningService,
                            RequestBuilder requestBuilder, RequestId requestId) {
        this.projectService = projectService;
        this.endpointService = endpointService;
        this.odahuDAO = odahuDAO;
        this.provisioningService = provisioningService;
        this.requestBuilder = requestBuilder;
        this.requestId = requestId;
    }


    @Override
    public List<OdahuDTO> findOdahu() {
        return odahuDAO.findOdahuClusters();
    }

    @BudgetLimited
    @Override
    public void create(@Project String project, OdahuActionDTO createOdahuDTO, UserInfo user) {
        Optional<OdahuDTO> odahuDTO = odahuDAO.getByProjectEndpoint(createOdahuDTO.getProject(), createOdahuDTO.getEndpoint());
        if (odahuDTO.isPresent()) {
            throw new ResourceConflictException(String.format("Odahu cluster already exist in system for project %s " +
                    "and endpoint %s", createOdahuDTO.getProject(), createOdahuDTO.getEndpoint()));
        }
        ProjectDTO projectDTO = projectService.get(project);
        boolean isAdded = odahuDAO.create(new OdahuDTO(createOdahuDTO.getName(), createOdahuDTO.getProject(),
                createOdahuDTO.getEndpoint(), UserInstanceStatus.CREATING));

        if (isAdded) {
            String url = null;
            try {
                url = endpointService.get(createOdahuDTO.getEndpoint()).getUrl() + CREATE_ODAHU_API;
                String uuid =
                        provisioningService.post(url, user.getAccessToken(),
                                requestBuilder.newOdahuCreate(user, createOdahuDTO, projectDTO), String.class);
                requestId.put(user.getName(), uuid);
            } catch (Exception e) {
                log.error("Can not perform {} due to: {}, {}", url, e.getMessage(), e);
                odahuDAO.updateStatus(createOdahuDTO.getName(), createOdahuDTO.getProject(), createOdahuDTO.getEndpoint(),
                        UserInstanceStatus.FAILED);
            }
        }
    }

    @BudgetLimited
    @Override
    public void start(@Project String project, OdahuActionDTO startOdahuDTO, UserInfo user) {
        ProjectDTO projectDTO = projectService.get(project);
        odahuDAO.updateStatus(startOdahuDTO.getName(), startOdahuDTO.getProject(), startOdahuDTO.getEndpoint(),
                UserInstanceStatus.STARTING);
        actionOnCloud(user, startOdahuDTO, projectDTO, START_ODAHU_API);
    }

    @Override
    public void stop(String project, OdahuActionDTO stopOdahuDTO, UserInfo user) {
        ProjectDTO projectDTO = projectService.get(project);
        odahuDAO.updateStatus(stopOdahuDTO.getName(), stopOdahuDTO.getProject(), stopOdahuDTO.getEndpoint(),
                UserInstanceStatus.STOPPING);
        actionOnCloud(user, stopOdahuDTO, projectDTO, STOP_ODAHU_API);
    }

    @Override
    public void terminate(String project, OdahuActionDTO terminateOdahuDTO, UserInfo user) {
        ProjectDTO projectDTO = projectService.get(project);
        odahuDAO.updateStatus(terminateOdahuDTO.getName(), terminateOdahuDTO.getProject(), terminateOdahuDTO.getEndpoint(),
                UserInstanceStatus.TERMINATING);
        actionOnCloud(user, terminateOdahuDTO, projectDTO, TERMINATE_ODAHU_API);
    }

    @Override
    public void updateStatus(OdahuResult result, UserInstanceStatus status) {
        if (Objects.nonNull(result.getResourceUrls()) && !result.getResourceUrls().isEmpty()) {
            odahuDAO.updateStatusAndUrls(result.getName(), result.getProjectName(), result.getEndpointName(),
                    result.getResourceUrls(), status);
        } else {
            odahuDAO.updateStatus(result.getName(), result.getProjectName(), result.getEndpointName(), status);
        }
    }

    private void actionOnCloud(UserInfo user, OdahuActionDTO odahuActionDTO, ProjectDTO projectDTO, String uri) {
        String url = null;
        try {
            url = endpointService.get(odahuActionDTO.getEndpoint()).getUrl() + uri;
            String uuid =
                    provisioningService.post(url, user.getAccessToken(),
                            requestBuilder.newOdahuAction(user, odahuActionDTO, projectDTO), String.class);
            requestId.put(user.getName(), uuid);
        } catch (Exception e) {
            log.error("Can not perform {} due to: {}, {}", url, e.getMessage(), e);
            odahuDAO.updateStatus(odahuActionDTO.getName(), odahuActionDTO.getProject(), odahuActionDTO.getEndpoint(),
                    UserInstanceStatus.FAILED);
        }
    }
}