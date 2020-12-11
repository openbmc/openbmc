/*
 * Copyright (c) 2020 Ampere Computing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once
#include <systemd/sd-journal.h>

#include <filesystem>
#include <fstream>
#include <functional>
#include <iomanip>
#include <iostream>
#include <memory>
#include <regex>
#include <string>
#include <tuple>
#include <utility>
#include <variant>
#include <vector>

#include <boost/algorithm/string.hpp>
#include <boost/container/flat_map.hpp>
#include <boost/container/flat_set.hpp>

namespace fs = std::filesystem;

#define IPMI_SEL_OEM_RECORD_TYPE                0xC0

namespace tempevent_log {
const std::filesystem::path selLogDir = "/var/log";
const std::string selLogFilename = "ipmi_sel";

// ID string generated using journalctl to include in the MESSAGE_ID field for
// SEL entries.  Helps with filtering SEL entries in the journal.
static constexpr char const *selMessageId = "b370836ccf2f4850ac5bee185b77893a";
static constexpr uint8_t selSystemType = 0x02;
static constexpr uint16_t selBMCGenID = 0x0020;
static constexpr uint16_t selInvalidRecID =
    std::numeric_limits<uint16_t>::max();
static constexpr size_t selEvtDataMaxSize = 3;
static constexpr size_t selOemDataMaxSize = 12;

static bool getSELLogFiles(std::vector<std::filesystem::path> &selLogFiles) {
  // Loop through the directory looking for ipmi_sel log files
  for (const std::filesystem::directory_entry &dirEnt :
       std::filesystem::directory_iterator(selLogDir)) {
    std::string filename = dirEnt.path().filename();
    if (boost::starts_with(filename, selLogFilename)) {
      // If we find an ipmi_sel log file, save the path
      selLogFiles.emplace_back(selLogDir / filename);
    }
  }
  // As the log files rotate, they are appended with a ".#" that is higher for
  // the older logs. Since we don't expect more than 10 log files, we
  // can just sort the list to get them in order from newest to oldest
  std::sort(selLogFiles.begin(), selLogFiles.end());

  return !selLogFiles.empty();
}

static unsigned int initializeRecordId(void) {
  std::vector<std::filesystem::path> selLogFiles;
  if (!getSELLogFiles(selLogFiles)) {
    return selInvalidRecID;
  }
  std::ifstream logStream(selLogFiles.front());
  if (!logStream.is_open()) {
    return selInvalidRecID;
  }
  std::string line;
  std::string newestEntry;
  while (std::getline(logStream, line)) {
    newestEntry = line;
  }

  std::vector<std::string> newestEntryFields;
  boost::split(newestEntryFields, newestEntry, boost::is_any_of(" ,"),
               boost::token_compress_on);
  if (newestEntryFields.size() < 4) {
    return selInvalidRecID;
  }

  return std::stoul(newestEntryFields[1]);
}

static unsigned int getNewRecordId(void) {
  static unsigned int recordId = initializeRecordId();

  // If the log has been cleared, also clear the current ID
  std::vector<std::filesystem::path> selLogFiles;
  if (!getSELLogFiles(selLogFiles)) {
    recordId = selInvalidRecID;
  }

  if (++recordId >= selInvalidRecID) {
    recordId = 1;
  }
  return recordId;
}

static void toHexStr(const std::vector<uint8_t> &data, std::string &hexStr) {
  std::stringstream stream;
  stream << std::hex << std::uppercase << std::setfill('0');
  for (const int &v : data) {
    stream << std::setw(2) << v;
  }

  hexStr = stream.str();
}

template <typename... T>
static uint16_t selAddOemRecord(const std::string &message,
                                const int selPriority,
                                const std::vector<uint8_t> &selData,
                                const uint8_t &recordType, T &&... metadata) {
  // A maximum of 13 bytes of SEL event data are allowed in an OEM record
  if (selData.size() > selOemDataMaxSize) {
    throw std::invalid_argument("Event data too large");
  }
  std::string selDataStr;
  toHexStr(selData, selDataStr);

  unsigned int recordId = getNewRecordId();
  sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i", selPriority,
                  "MESSAGE_ID=%s", selMessageId, "IPMI_SEL_RECORD_ID=%d",
                  recordId, "IPMI_SEL_RECORD_TYPE=%x", recordType,
                  "IPMI_SEL_DATA=%s", selDataStr.c_str(),
                  std::forward<T>(metadata)..., NULL);
  return recordId;
}

}  // namespace tempevent_log
