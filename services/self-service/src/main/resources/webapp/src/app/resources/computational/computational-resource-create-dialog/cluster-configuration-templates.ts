/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

export const CLUSTER_CONFIGURATION = {
  EMR: [{
    Classification: 'classification-name',
    Properties: {
      property_name: 'property_value'
    }
  }, {
    Classification: 'classification-name',
    Properties: {
      property_name: 'property_value',
      property_name_x: 'property_value'
    }
  }],
  SPARK: [{
    Classification: 'spark-defaults',
    Properties: {
      property_name: 'property_value'
    }
  }]
};