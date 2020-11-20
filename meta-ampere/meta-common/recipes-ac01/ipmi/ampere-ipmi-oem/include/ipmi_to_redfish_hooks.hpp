/*
// Copyright (c) 2019 Intel Corporation
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
#include <boost/algorithm/string/join.hpp>
#include <phosphor-logging/log.hpp>
#include <storagecommands.hpp>

namespace ampere_oem::ipmi::sel
{
bool checkRedfishHooks(uint16_t recordID, uint8_t recordType,
                       uint32_t timestamp, uint16_t generatorID, uint8_t evmRev,
                       uint8_t sensorType, uint8_t sensorNum, uint8_t eventType,
                       uint8_t eventData1, uint8_t eventData2,
                       uint8_t eventData3);
bool checkRedfishHooks(uint8_t generatorID, uint8_t evmRev, uint8_t sensorType,
                       uint8_t sensorNum, uint8_t eventType, uint8_t eventData1,
                       uint8_t eventData2, uint8_t eventData3);
namespace redfish_hooks
{
struct SELData
{
    int generatorID;
    int sensorNum;
    int eventType;
    int offset;
    int eventData2;
    int eventData3;
};

static inline bool defaultMessageHook(const std::string& ipmiRaw)
{
    // Log the record as a default Redfish message instead of a SEL record

    static const std::string openBMCMessageRegistryVersion("0.1");
    std::string messageID =
        "OpenBMC." + openBMCMessageRegistryVersion + ".SELEntryAdded";

    std::vector<std::string> messageArgs;
    messageArgs.push_back(ipmiRaw);

    // Log the Redfish message to the journal with the appropriate metadata
    std::string journalMsg = "SEL Entry Added: " + ipmiRaw;
    std::string messageArgsString = boost::algorithm::join(messageArgs, ",");
    phosphor::logging::log<phosphor::logging::level::INFO>(
        journalMsg.c_str(),
        phosphor::logging::entry("REDFISH_MESSAGE_ID=%s", messageID.c_str()),
        phosphor::logging::entry("REDFISH_MESSAGE_ARGS=%s",
                                 messageArgsString.c_str()));

    return true;
}

} // namespace redfish_hooks
}
