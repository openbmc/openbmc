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
const static constexpr u_int8_t NUM_SOCKET      = 2;

std::string hwmonRootDir[2]     = {
        "/sys/devices/platform/ahb/ahb:apb/ahb:apb:bus@1e78a000/"\
        "1e78a0c0.i2c-bus/i2c-2/2-004f/1e78a0c0.i2c-bus:smpro@4f:errmon/",
        "/sys/devices/platform/ahb/ahb:apb/ahb:apb:bus@1e78a000/"\
        "1e78a0c0.i2c-bus/i2c-2/2-004e/1e78a0c0.i2c-bus:smpro@4e:errmon/"
        };

static std::string getAbsolutePath(u_int8_t socket, std::string fileName)
{
    if (hwmonRootDir[socket] != ""){
        return hwmonRootDir[socket] + fileName;
    }
    return "";
}

static int initHwmonRootPath()
{
    u_int8_t socket = 0;
    bool foundRootPath = false;
    char path[256];
    for (u_int8_t socket=0; socket < NUM_SOCKET; socket++)
    {
        auto path = fs::path(hwmonRootDir[socket]);
        if (fs::exists(path) && fs::is_directory(path))
        {
            path = fs::path(hwmonRootDir[socket] + "/errors_core_ce");
            if (fs::exists(path))
            {
                foundRootPath = true;
                continue;
            }
        }
        hwmonRootDir[socket] = "";
    }
    if (foundRootPath)
    {
        return 1;
    }
    return 0;
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
