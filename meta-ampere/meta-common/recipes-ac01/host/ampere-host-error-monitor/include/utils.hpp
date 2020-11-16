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

#include <filesystem>
#include <fstream>
#include <functional>
#include <iomanip>
#include <iostream>
#include <memory>
#include <regex>
#include <string>
#include <utility>
#include <variant>
#include <vector>

#include <boost/algorithm/string.hpp>
#include <boost/container/flat_map.hpp>
#include <boost/container/flat_set.hpp>
#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/elog.hpp>
#include <phosphor-logging/log.hpp>

namespace fs = std::filesystem;

namespace ampere
{
namespace utils
{
using namespace phosphor::logging;
namespace fs = std::filesystem;

const static constexpr char *HWMON_ROOT_PATH    = "/sys/class/hwmon";
const static constexpr u_int8_t NUM_SOCKET      = 2;
constexpr size_t i2cScpAddr[2]  = {0x4f, 0x4e};
constexpr size_t i2cScpBus      = 2;
std::string hwmonRootDir[2]     = {"",""};

static bool findFiles(const fs::path& dirPath, const std::string& matchString,
                      std::vector<fs::path>& foundPaths,
                      unsigned int symlinkDepth)
{
    if (!fs::exists(dirPath))
        return false;

    std::regex expr(matchString);
    for (auto& p : fs::recursive_directory_iterator(dirPath))
    {
        std::string path = p.path().string();
        if (!fs::is_directory(p))
        {
            if (std::regex_search(path, expr))
                foundPaths.emplace_back(p.path());
        }
        else if (fs::is_symlink(p) && symlinkDepth)
        {
            findFiles(p.path(), matchString, foundPaths, symlinkDepth - 1);
        }
    }

    return true;
}

static std::optional<std::string> getRootDir(const char* parentPath,
    const char* fileName, unsigned int cpuSocket)
{
    if (cpuSocket > sizeof(i2cScpAddr)/sizeof(i2cScpAddr[0]))
        return std::nullopt;

    std::vector<fs::path> paths;
    if (!findFiles(fs::path(parentPath), fileName, paths, 1))
        return std::nullopt;

    /* Get root path of RAS APIs corresponding to CPU socket */
    for (const auto& path : paths)
    {
        fs::path device = path.parent_path() / "device";
        std::string deviceName = fs::canonical(device).stem();
        std::vector<std::string> parsedName;
        boost::split(parsedName, deviceName, [](char c) { return c == '-'; });
        if (parsedName.size() != 2)
            continue;
        size_t bus = 0;
        size_t addr = 0;
        try
        {
            bus = std::stoi(parsedName[0]);
            addr = std::stoi(parsedName[1], 0, 16);
        }
        catch (std::invalid_argument&)
        {
            continue;
        }

        if ((bus == i2cScpBus) && (addr == i2cScpAddr[cpuSocket]))
            return path.string().replace(path.string().find(fileName),
                strlen(fileName), "");
    }

    return std::nullopt;
}

static std::string getAbsolutePath(u_int8_t socket, std::string fileName)
{
    return hwmonRootDir[socket] + fileName;
}

static int initHwmonRootPath()
{
    u_int8_t socket = 0;
    char buff[128];
    for (u_int8_t socket=0; socket < NUM_SOCKET; socket++)
    {
        auto path = getRootDir(HWMON_ROOT_PATH, "uevent", socket);
        if (path == std::nullopt) {
            sprintf(buff, "Unable to get root hwmon path of socket %d\n",
               socket);
            log<level::ERR>(buff);
            return 0;
        }
        hwmonRootDir[socket] = path.value();
    }
    return 1;
}

static u_int64_t parseHexStrToUInt64(std::string str)
{
    char* p;

    long n = strtoull(str.c_str(), &p, 16);
    if ( *p != 0 )
        return 0;

    return n & 0xffffffffffffffff;
}

static u_int32_t parseHexStrToUInt32(std::string str)
{
    char* p;

    long n = strtoul(str.c_str(), &p, 16);
    if ( *p != 0 )
        return 0;

    return n & 0xffffffff;
}

static u_int16_t parseHexStrToUInt16(std::string str)
{
    char* p;

    long n = strtoul(str.c_str(), &p, 16);
    if ( *p != 0 )
        return 0;

    return n & 0xffff;
}

static u_int8_t parseHexStrToUInt8(std::string str)
{
    char* p;

    long n = strtoul(str.c_str(), &p, 16);
    if ( *p != 0 )
        return 0;

    return n & 0xff;
}

} /* namespace utils */
} /* namespace ampere */
