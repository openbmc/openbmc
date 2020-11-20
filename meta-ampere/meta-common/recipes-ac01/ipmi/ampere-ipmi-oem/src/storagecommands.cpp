/*
// Copyright (c) 2017-2019 Intel Corporation
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

#include "storagecommands.hpp"

#include "commandutils.hpp"
#include "ipmi_to_redfish_hooks.hpp"
#include "sdrutils.hpp"
#include "types.hpp"

#include <boost/algorithm/string.hpp>
#include <boost/container/flat_map.hpp>
#include <boost/process.hpp>
#include <ipmid/api.hpp>
#include <ipmid/message.hpp>
#include <phosphor-ipmi-host/selutility.hpp>
#include <phosphor-logging/log.hpp>
#include <sdbusplus/message/types.hpp>
#include <sdbusplus/timer.hpp>

#include <filesystem>
#include <functional>
#include <iostream>
#include <stdexcept>
#include <string_view>

static constexpr bool DEBUG = false;

namespace ampere_oem::ipmi::sel
{
static const std::filesystem::path selLogDir = "/var/log";
static const std::string selLogFilename = "ipmi_sel";

static int getFileTimestamp(const std::filesystem::path& file)
{
    struct stat st;

    if (stat(file.c_str(), &st) >= 0)
    {
        return st.st_mtime;
    }
    return ::ipmi::sel::invalidTimeStamp;
}

namespace erase_time
{
static constexpr const char* selEraseTimestamp = "/var/lib/ipmi/sel_erase_time";

void save()
{
    // open the file, creating it if necessary
    int fd = open(selEraseTimestamp, O_WRONLY | O_CREAT | O_CLOEXEC, 0644);
    if (fd < 0)
    {
        std::cerr << "Failed to open file\n";
        return;
    }

    // update the file timestamp to the current time
    if (futimens(fd, NULL) < 0)
    {
        std::cerr << "Failed to update timestamp: "
                  << std::string(strerror(errno));
    }
    close(fd);
}

int get()
{
    return getFileTimestamp(selEraseTimestamp);
}
} // namespace erase_time
} // namespace ampere_oem::ipmi::sel

namespace ipmi
{

namespace storage
{

// event direction is bit[7] of eventType where 1b = Deassertion event
constexpr static const uint8_t deassertionEvent = 0x80;

static std::vector<uint8_t> fruCache;
static uint8_t cacheBus = 0xFF;
static uint8_t cacheAddr = 0XFF;
static uint8_t lastDevId = 0xFF;

static uint8_t writeBus = 0xFF;
static uint8_t writeAddr = 0XFF;

std::unique_ptr<phosphor::Timer> writeTimer = nullptr;


// we unfortunately have to build a map of hashes in case there is a
// collision to verify our dev-id
boost::container::flat_map<uint8_t, std::pair<uint8_t, uint8_t>> deviceHashes;

void registerStorageFunctions() __attribute__((constructor));

static bool getSELLogFiles(std::vector<std::filesystem::path>& selLogFiles)
{
    // Loop through the directory looking for ipmi_sel log files
    for (const std::filesystem::directory_entry& dirEnt :
         std::filesystem::directory_iterator(ampere_oem::ipmi::sel::selLogDir))
    {
        std::string filename = dirEnt.path().filename();
        if (boost::starts_with(filename, ampere_oem::ipmi::sel::selLogFilename))
        {
            // If we find an ipmi_sel log file, save the path
            selLogFiles.emplace_back(ampere_oem::ipmi::sel::selLogDir /
                                     filename);
        }
    }
    // As the log files rotate, they are appended with a ".#" that is higher for
    // the older logs. Since we don't expect more than 10 log files, we
    // can just sort the list to get them in order from newest to oldest
    std::sort(selLogFiles.begin(), selLogFiles.end());

    return !selLogFiles.empty();
}

static int countSELEntries()
{
    // Get the list of ipmi_sel log files
    std::vector<std::filesystem::path> selLogFiles;
    if (!getSELLogFiles(selLogFiles))
    {
        return 0;
    }
    int numSELEntries = 0;
    // Loop through each log file and count the number of logs
    for (const std::filesystem::path& file : selLogFiles)
    {
        std::ifstream logStream(file);
        if (!logStream.is_open())
        {
            continue;
        }

        std::string line;
        while (std::getline(logStream, line))
        {
            numSELEntries++;
        }
    }
    return numSELEntries;
}

static bool findSELEntry(const int recordID,
                         const std::vector<std::filesystem::path>& selLogFiles,
                         std::string& entry)
{
    // Record ID is the first entry field following the timestamp. It is
    // preceded by a space and followed by a comma
    std::string search = " " + std::to_string(recordID) + ",";

    // Loop through the ipmi_sel log entries
    for (const std::filesystem::path& file : selLogFiles)
    {
        std::ifstream logStream(file);
        if (!logStream.is_open())
        {
            continue;
        }

        while (std::getline(logStream, entry))
        {
            // Check if the record ID matches
            if (entry.find(search) != std::string::npos)
            {
                return true;
            }
        }
    }
    return false;
}

static uint16_t
    getNextRecordID(const uint16_t recordID,
                    const std::vector<std::filesystem::path>& selLogFiles)
{
    uint16_t nextRecordID = recordID + 1;
    std::string entry;
    if (findSELEntry(nextRecordID, selLogFiles, entry))
    {
        return nextRecordID;
    }
    else
    {
        return ipmi::sel::lastEntry;
    }
}

static int fromHexStr(const std::string& hexStr, std::vector<uint8_t>& data)
{
    for (unsigned int i = 0; i < hexStr.size(); i += 2)
    {
        try
        {
            data.push_back(static_cast<uint8_t>(
                std::stoul(hexStr.substr(i, 2), nullptr, 16)));
        }
        catch (std::invalid_argument& e)
        {
            phosphor::logging::log<phosphor::logging::level::ERR>(e.what());
            return -1;
        }
        catch (std::out_of_range& e)
        {
            phosphor::logging::log<phosphor::logging::level::ERR>(e.what());
            return -1;
        }
    }
    return 0;
}

ipmi::RspType<uint8_t,  // SEL version
              uint16_t, // SEL entry count
              uint16_t, // free space
              uint32_t, // last add timestamp
              uint32_t, // last erase timestamp
              uint8_t>  // operation support
    ipmiStorageGetSELInfo()
{
    constexpr uint8_t selVersion = ipmi::sel::selVersion;
    uint16_t entries = countSELEntries();
    uint32_t addTimeStamp = ampere_oem::ipmi::sel::getFileTimestamp(
        ampere_oem::ipmi::sel::selLogDir / ampere_oem::ipmi::sel::selLogFilename);
    uint32_t eraseTimeStamp = ampere_oem::ipmi::sel::erase_time::get();
    constexpr uint8_t operationSupport =
        ampere_oem::ipmi::sel::selOperationSupport;
    constexpr uint16_t freeSpace =
        0xffff; // Spec indicates that more than 64kB is free

    return ipmi::responseSuccess(selVersion, entries, freeSpace, addTimeStamp,
                                 eraseTimeStamp, operationSupport);
}

using systemEventType = std::tuple<
    uint32_t, // Timestamp
    uint16_t, // Generator ID
    uint8_t,  // EvM Rev
    uint8_t,  // Sensor Type
    uint8_t,  // Sensor Number
    uint7_t,  // Event Type
    bool,     // Event Direction
    std::array<uint8_t, ampere_oem::ipmi::sel::systemEventSize>>; // Event Data
using oemTsEventType = std::tuple<
    uint32_t,                                                   // Timestamp
    std::array<uint8_t, ampere_oem::ipmi::sel::oemTsEventSize>>; // Event Data
using oemEventType =
    std::array<uint8_t, ampere_oem::ipmi::sel::oemEventSize>; // Event Data

ipmi::RspType<uint16_t, // Next Record ID
              uint16_t, // Record ID
              uint8_t,  // Record Type
              std::variant<systemEventType, oemTsEventType,
                           oemEventType>> // Record Content
    ipmiStorageGetSELEntry(uint16_t reservationID, uint16_t targetID,
                           uint8_t offset, uint8_t size)
{
    // Only support getting the entire SEL record. If a partial size or non-zero
    // offset is requested, return an error
    if (offset != 0 || size != ipmi::sel::entireRecord)
    {
        return ipmi::responseRetBytesUnavailable();
    }

    // Check the reservation ID if one is provided or required (only if the
    // offset is non-zero)
    if (reservationID != 0 || offset != 0)
    {
        if (!checkSELReservation(reservationID))
        {
            return ipmi::responseInvalidReservationId();
        }
    }

    // Get the ipmi_sel log files
    std::vector<std::filesystem::path> selLogFiles;
    if (!getSELLogFiles(selLogFiles))
    {
        return ipmi::responseSensorInvalid();
    }

    std::string targetEntry;

    if (targetID == ipmi::sel::firstEntry)
    {
        // The first entry will be at the top of the oldest log file
        std::ifstream logStream(selLogFiles.back());
        if (!logStream.is_open())
        {
            return ipmi::responseUnspecifiedError();
        }

        if (!std::getline(logStream, targetEntry))
        {
            return ipmi::responseUnspecifiedError();
        }
    }
    else if (targetID == ipmi::sel::lastEntry)
    {
        // The last entry will be at the bottom of the newest log file
        std::ifstream logStream(selLogFiles.front());
        if (!logStream.is_open())
        {
            return ipmi::responseUnspecifiedError();
        }

        std::string line;
        while (std::getline(logStream, line))
        {
            targetEntry = line;
        }
    }
    else
    {
        if (!findSELEntry(targetID, selLogFiles, targetEntry))
        {
            return ipmi::responseSensorInvalid();
        }
    }

    // The format of the ipmi_sel message is "<Timestamp>
    // <ID>,<Type>,<EventData>,[<Generator ID>,<Path>,<Direction>]".
    // First get the Timestamp
    size_t space = targetEntry.find_first_of(" ");
    if (space == std::string::npos)
    {
        return ipmi::responseUnspecifiedError();
    }
    std::string entryTimestamp = targetEntry.substr(0, space);
    // Then get the log contents
    size_t entryStart = targetEntry.find_first_not_of(" ", space);
    if (entryStart == std::string::npos)
    {
        return ipmi::responseUnspecifiedError();
    }
    std::string_view entry(targetEntry);
    entry.remove_prefix(entryStart);
    // Use split to separate the entry into its fields
    std::vector<std::string> targetEntryFields;
    boost::split(targetEntryFields, entry, boost::is_any_of(","),
                 boost::token_compress_on);
    if (targetEntryFields.size() < 3)
    {
        return ipmi::responseUnspecifiedError();
    }
    std::string& recordIDStr = targetEntryFields[0];
    std::string& recordTypeStr = targetEntryFields[1];
    std::string& eventDataStr = targetEntryFields[2];

    uint16_t recordID;
    uint8_t recordType;
    try
    {
        recordID = std::stoul(recordIDStr);
        recordType = std::stoul(recordTypeStr, nullptr, 16);
    }
    catch (const std::invalid_argument&)
    {
        return ipmi::responseUnspecifiedError();
    }
    uint16_t nextRecordID = getNextRecordID(recordID, selLogFiles);
    std::vector<uint8_t> eventDataBytes;
    if (fromHexStr(eventDataStr, eventDataBytes) < 0)
    {
        return ipmi::responseUnspecifiedError();
    }

    if (recordType == ampere_oem::ipmi::sel::systemEvent)
    {
        // Get the timestamp
        std::tm timeStruct = {};
        std::istringstream entryStream(entryTimestamp);

        uint32_t timestamp = ipmi::sel::invalidTimeStamp;
        if (entryStream >> std::get_time(&timeStruct, "%Y-%m-%dT%H:%M:%S"))
        {
            timestamp = std::mktime(&timeStruct);
        }

        // Set the event message revision
        uint8_t evmRev = ampere_oem::ipmi::sel::eventMsgRev;

        uint16_t generatorID = 0;
        uint8_t sensorType = 0;
        uint8_t sensorNum = 0xFF;
        uint7_t eventType = 0;
        bool eventDir = 0;
        // System type events should have six fields
        if (targetEntryFields.size() >= 6)
        {
            std::string& generatorIDStr = targetEntryFields[3];
            std::string& sensorPath = targetEntryFields[4];
            std::string& eventDirStr = targetEntryFields[5];

            // Get the generator ID
            try
            {
                generatorID = std::stoul(generatorIDStr, nullptr, 16);
            }
            catch (const std::invalid_argument&)
            {
                std::cerr << "Invalid Generator ID\n";
            }

            // Get the sensor type, sensor number, and event type for the sensor
            sensorType = getSensorTypeFromPath(sensorPath);
            sensorNum = getSensorNumberFromPath(sensorPath);
            eventType = getSensorEventTypeFromPath(sensorPath);

            // Get the event direction
            try
            {
                eventDir = std::stoul(eventDirStr) ? 0 : 1;
            }
            catch (const std::invalid_argument&)
            {
                std::cerr << "Invalid Event Direction\n";
            }
        }

        // Only keep the eventData bytes that fit in the record
        std::array<uint8_t, ampere_oem::ipmi::sel::systemEventSize> eventData{};
        std::copy_n(eventDataBytes.begin(),
                    std::min(eventDataBytes.size(), eventData.size()),
                    eventData.begin());

        return ipmi::responseSuccess(
            nextRecordID, recordID, recordType,
            systemEventType{timestamp, generatorID, evmRev, sensorType,
                            sensorNum, eventType, eventDir, eventData});
    }
    else if (recordType >= ampere_oem::ipmi::sel::oemTsEventFirst &&
             recordType <= ampere_oem::ipmi::sel::oemTsEventLast)
    {
        // Get the timestamp
        std::tm timeStruct = {};
        std::istringstream entryStream(entryTimestamp);

        uint32_t timestamp = ipmi::sel::invalidTimeStamp;
        if (entryStream >> std::get_time(&timeStruct, "%Y-%m-%dT%H:%M:%S"))
        {
            timestamp = std::mktime(&timeStruct);
        }

        // Only keep the bytes that fit in the record
        std::array<uint8_t, ampere_oem::ipmi::sel::oemTsEventSize> eventData{};
        std::copy_n(eventDataBytes.begin(),
                    std::min(eventDataBytes.size(), eventData.size()),
                    eventData.begin());

        return ipmi::responseSuccess(nextRecordID, recordID, recordType,
                                     oemTsEventType{timestamp, eventData});
    }
    else if (recordType >= ampere_oem::ipmi::sel::oemEventFirst)
    {
        // Only keep the bytes that fit in the record
        std::array<uint8_t, ampere_oem::ipmi::sel::oemEventSize> eventData{};
        std::copy_n(eventDataBytes.begin(),
                    std::min(eventDataBytes.size(), eventData.size()),
                    eventData.begin());

        return ipmi::responseSuccess(nextRecordID, recordID, recordType,
                                     eventData);
    }

    return ipmi::responseUnspecifiedError();
}

ipmi::RspType<uint16_t> ipmiStorageAddSELEntry(
    uint16_t recordID, uint8_t recordType, uint32_t timestamp,
    uint16_t generatorID, uint8_t evmRev, uint8_t sensorType, uint8_t sensorNum,
    uint8_t eventType, uint8_t eventData1, uint8_t eventData2,
    uint8_t eventData3)
{
    // Per the IPMI spec, need to cancel any reservation when a SEL entry is
    // added
    cancelSELReservation();

    // Send this request to the Redfish hooks to log it as a Redfish message
    // instead.  There is no need to add it to the SEL, so just return success.
    ampere_oem::ipmi::sel::checkRedfishHooks(
        recordID, recordType, timestamp, generatorID, evmRev, sensorType,
        sensorNum, eventType, eventData1, eventData2, eventData3);

    uint16_t responseID = 0xFFFF;
    return ipmi::responseSuccess(responseID);
}

ipmi::RspType<uint8_t> ipmiStorageClearSEL(ipmi::Context::ptr ctx,
                                           uint16_t reservationID,
                                           const std::array<uint8_t, 3>& clr,
                                           uint8_t eraseOperation)
{
    if (!checkSELReservation(reservationID))
    {
        return ipmi::responseInvalidReservationId();
    }

    static constexpr std::array<uint8_t, 3> clrExpected = {'C', 'L', 'R'};
    if (clr != clrExpected)
    {
        return ipmi::responseInvalidFieldRequest();
    }

    // Erasure status cannot be fetched, so always return erasure status as
    // `erase completed`.
    if (eraseOperation == ipmi::sel::getEraseStatus)
    {
        return ipmi::responseSuccess(ipmi::sel::eraseComplete);
    }

    // Check that initiate erase is correct
    if (eraseOperation != ipmi::sel::initiateErase)
    {
        return ipmi::responseInvalidFieldRequest();
    }

    // Per the IPMI spec, need to cancel any reservation when the SEL is
    // cleared
    cancelSELReservation();

    // Save the erase time
    ampere_oem::ipmi::sel::erase_time::save();

    // Clear the SEL by deleting the log files
    std::vector<std::filesystem::path> selLogFiles;
    if (getSELLogFiles(selLogFiles))
    {
        for (const std::filesystem::path& file : selLogFiles)
        {
            std::error_code ec;
            std::filesystem::remove(file, ec);
        }
    }

    // Reload rsyslog so it knows to start new log files
    std::shared_ptr<sdbusplus::asio::connection> dbus = getSdBus();
    sdbusplus::message::message rsyslogReload = dbus->new_method_call(
        "org.freedesktop.systemd1", "/org/freedesktop/systemd1",
        "org.freedesktop.systemd1.Manager", "ReloadUnit");
    rsyslogReload.append("rsyslog.service", "replace");
    try
    {
        sdbusplus::message::message reloadResponse = dbus->call(rsyslogReload);
    }
    catch (sdbusplus::exception_t& e)
    {
        phosphor::logging::log<phosphor::logging::level::ERR>(e.what());
    }

    return ipmi::responseSuccess(ipmi::sel::eraseComplete);
}

void registerStorageFunctions()
{

    // <Get SEL Info>
    ipmi::registerHandler(ipmi::prioOpenBmcBase, ipmi::netFnStorage,
                          ipmi::storage::cmdGetSelInfo, ipmi::Privilege::User,
                          ipmiStorageGetSELInfo);

    // <Get SEL Entry>
    ipmi::registerHandler(ipmi::prioOpenBmcBase, ipmi::netFnStorage,
                          ipmi::storage::cmdGetSelEntry, ipmi::Privilege::User,
                          ipmiStorageGetSELEntry);

    // <Add SEL Entry>
    ipmi::registerHandler(ipmi::prioOpenBmcBase, ipmi::netFnStorage,
                          ipmi::storage::cmdAddSelEntry,
                          ipmi::Privilege::Operator, ipmiStorageAddSELEntry);

    // <Clear SEL>
    ipmi::registerHandler(ipmi::prioOpenBmcBase, ipmi::netFnStorage,
                          ipmi::storage::cmdClearSel, ipmi::Privilege::Operator,
                          ipmiStorageClearSEL);

}
} // namespace storage
} // namespace ipmi
