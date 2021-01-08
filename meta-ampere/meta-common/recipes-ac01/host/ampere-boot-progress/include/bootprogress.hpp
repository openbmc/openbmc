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

namespace bootprogress
{
    const std::string BOOT_PROGRESS_FS          = "/sys/bus/i2c/devices/2-004f/1e78a0c0.i2c-bus:smpro@4f:misc/boot_progress";

    const uint8_t S0_SMPRO_I2C_ADDR             = 0x4F;
    const uint8_t S1_SMPRO_I2C_ADDR             = 0x4E;
    const uint8_t SMPRO_I2C_BUS                 = 0x2;

    enum STAGES
    {
        BOOT_STAGE_SMPRO = 0,
        BOOT_STAGE_PMPRO,
        BOOT_STAGE_ATF_BL1,
        BOOT_STAGE_DDR_INIT,
        BOOT_STAGE_DDR_INIT_PROGRESS,
        BOOT_STAGE_ATF_BL2,
        BOOT_STAGE_ATF_BL31,
        BOOT_STAGE_ATF_BL32,
        BOOT_STAGE_UEFI,
        BOOT_STAGE_OS = 9
    };

    enum STATUS
    {
        BOOT_STATUS_NOT_STARTED = 0,
        BOOT_STATUS_STARTED,
        BOOT_STATUS_COMPLETED_OK,
        BOOT_STATUS_FAILURE,
        BOOT_STATUS_MAX_STATE,
    };

    static void handleBootProgress();

}
