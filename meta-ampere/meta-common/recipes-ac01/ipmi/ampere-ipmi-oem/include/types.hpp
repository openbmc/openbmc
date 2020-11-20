/*
// Copyright (c) 2017 2018 Intel Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/

#pragma once

#include <map>
#include <string>
#include <tuple>
#include <utility>
#include <variant>
#include <vector>

namespace ipmi
{

using Association = std::tuple<std::string, std::string, std::string>;

using DbusVariant =
    std::variant<std::string, bool, uint8_t, uint16_t, int16_t, uint32_t,
                 int32_t, uint64_t, int64_t, double, std::vector<Association>>;

using GetSubTreeType = std::vector<
    std::pair<std::string,
              std::vector<std::pair<std::string, std::vector<std::string>>>>>;

using SensorMap = std::map<std::string, std::map<std::string, DbusVariant>>;

} // namespace ipmi
