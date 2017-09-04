/*
 * Copyright (c) 2017, EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.dlab.dto.azure.keyload;

import com.epam.dlab.dto.EdgeInfoAware;
import com.epam.dlab.dto.StatusBaseDTO;
import com.epam.dlab.dto.azure.edge.EdgeInfoAzure;
import com.epam.dlab.dto.base.EdgeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class UploadFileResultAzure extends StatusBaseDTO<UploadFileResultAzure> implements EdgeInfoAware<EdgeInfoAzure> {

    @JsonProperty
    private EdgeInfoAzure edgeInfo;

    @Override
    public void populateEdgeInfo(EdgeInfoAzure edgeInfo) {
        this.edgeInfo = edgeInfo;
    }

    @Override
    public EdgeInfo retrieveEdgeInfo() {
        return edgeInfo;
    }

    @Override
    public MoreObjects.ToStringHelper toStringHelper(Object self) {
        return super.toStringHelper(self)
                .add("edgeInfo", edgeInfo);
    }

    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }
}
