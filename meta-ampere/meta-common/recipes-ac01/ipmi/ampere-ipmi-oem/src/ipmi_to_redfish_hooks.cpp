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

#include <boost/beast/core/span.hpp>
#include <ipmi_to_redfish_hooks.hpp>
#include <storagecommands.hpp>

#include <iomanip>
#include <sstream>
#include <string_view>

namespace ampere_oem::ipmi::sel
{

namespace redfish_hooks
{
static void toHexStr(const boost::beast::span<uint8_t> bytes,
                     std::string& hexStr)
{
    std::stringstream stream;
    stream << std::hex << std::uppercase << std::setfill('0');
    for (const uint8_t& byte : bytes)
    {
        stream << std::setw(2) << static_cast<int>(byte);
    }
    hexStr = stream.str();
}

static bool startRedfishHook(const SELData& selData, const std::string& ipmiRaw)
{
    // No hooks handled the request, so let it go to default
    return defaultMessageHook(ipmiRaw);
}
} // namespace redfish_hooks

bool checkRedfishHooks(uint16_t recordID, uint8_t recordType,
                       uint32_t timestamp, uint16_t generatorID, uint8_t evmRev,
                       uint8_t sensorType, uint8_t sensorNum, uint8_t eventType,
                       uint8_t eventData1, uint8_t eventData2,
                       uint8_t eventData3)
{
    // Save the raw IPMI string of the request
    std::string ipmiRaw;
    std::array selBytes = {static_cast<uint8_t>(recordID),
                           static_cast<uint8_t>(recordID >> 8),
                           recordType,
                           static_cast<uint8_t>(timestamp),
                           static_cast<uint8_t>(timestamp >> 8),
                           static_cast<uint8_t>(timestamp >> 16),
                           static_cast<uint8_t>(timestamp >> 24),
                           static_cast<uint8_t>(generatorID),
                           static_cast<uint8_t>(generatorID >> 8),
                           evmRev,
                           sensorType,
                           sensorNum,
                           eventType,
                           eventData1,
                           eventData2,
                           eventData3};
    redfish_hooks::toHexStr(boost::beast::span<uint8_t>(selBytes), ipmiRaw);

    // First check that this is a system event record type since that
    // determines the definition of the rest of the data
    if (recordType != ipmi::sel::systemEvent)
    {
        // OEM record type, so let it go to the SEL
        return redfish_hooks::defaultMessageHook(ipmiRaw);
    }

    // Extract the SEL data for the hook
    redfish_hooks::SELData selData = {.generatorID = generatorID,
                                      .sensorNum = sensorNum,
                                      .eventType = eventType,
                                      .offset = eventData1 & 0x0F,
                                      .eventData2 = eventData2,
                                      .eventData3 = eventData3};

    return redfish_hooks::startRedfishHook(selData, ipmiRaw);
}

bool checkRedfishHooks(uint8_t generatorID, uint8_t evmRev, uint8_t sensorType,
                       uint8_t sensorNum, uint8_t eventType, uint8_t eventData1,
                       uint8_t eventData2, uint8_t eventData3)
{
    // Save the raw IPMI string of the selData
    std::string ipmiRaw;
    std::array selBytes = {generatorID, evmRev,     sensorType, sensorNum,
                           eventType,   eventData1, eventData2, eventData3};
    redfish_hooks::toHexStr(boost::beast::span<uint8_t>(selBytes), ipmiRaw);

    // Extract the SEL data for the hook
    redfish_hooks::SELData selData = {.generatorID = generatorID,
                                      .sensorNum = sensorNum,
                                      .eventType = eventType,
                                      .offset = eventData1 & 0x0F,
                                      .eventData2 = eventData2,
                                      .eventData3 = eventData3};

    return redfish_hooks::startRedfishHook(selData, ipmiRaw);
}

} // namespace ampere_oem::ipmi::sel
