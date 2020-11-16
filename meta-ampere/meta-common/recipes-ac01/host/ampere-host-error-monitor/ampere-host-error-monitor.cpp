/*
 * Copyright (c) 2020 Ampere Computing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/elog.hpp>
#include <phosphor-logging/log.hpp>

#include <filesystem>
#include <fstream>
#include <iostream>
#include <memory>
#include <regex>
#include <gpiod.hpp>
#include "utils.hpp"
#include "selUtils.hpp"
#include <map>
#include <ctime>
#include <chrono>

using namespace phosphor::logging;

namespace ampere
{
namespace ras
{
    const static constexpr u_int16_t MAX_MSG_LEN    = 128;
    const static constexpr u_int8_t TYPE_OTHER      = 0x12;
    const static constexpr u_int8_t TYPE_MEM        = 0x0C;
    const static constexpr u_int8_t TYPE_CORE       = 0x07;
    const static constexpr u_int8_t TYPE_PCIE       = 0x13;
    const static constexpr u_int8_t CE_CORE_IERR    = 139;
    const static constexpr u_int8_t UE_CORE_IERR    = 140;
    const static constexpr u_int8_t CE_OTHER_IERR   = 141;
    const static constexpr u_int8_t UE_OTHER_IERR   = 142;
    const static constexpr u_int8_t CE_MEM_IERR     = 151;
    const static constexpr u_int8_t UE_MEM_IERR     = 168;
    const static constexpr u_int8_t CE_PCIE_IERR    = 191;
    const static constexpr u_int8_t UE_PCIE_IERR    = 202;

    struct ErrorFields {
        u_int8_t errType;
        u_int8_t subType;
        u_int16_t instance;
        u_int32_t status;
        u_int64_t address;
    };

    struct ErrorData {
        u_int16_t socket;
        const char* label;
        u_int8_t errType;
        u_int8_t errNum;
        const char* errName;
    };

    ErrorData errorTypeTable[] = {
        {0, "errors_core_ue", TYPE_CORE, UE_CORE_IERR, "UE_CPU_IError"},
        {0, "errors_mem_ue", TYPE_MEM, UE_MEM_IERR, "UE_Memory_IErr"},
        {0, "errors_pcie_ue", TYPE_PCIE, UE_PCIE_IERR, "UE_PCIE_IErr"},
        {0, "errors_other_ue", TYPE_OTHER, UE_OTHER_IERR, "UE_SoC_IErr"},
        {1, "errors_core_ue", TYPE_CORE, UE_CORE_IERR, "UE_CPU_IError"},
        {1, "errors_mem_ue", TYPE_MEM, UE_MEM_IERR, "UE_Memory_IErr"},
        {1, "errors_pcie_ue", TYPE_PCIE, UE_PCIE_IERR, "UE_PCIE_IErr"},
        {1, "errors_other_ue", TYPE_OTHER, UE_OTHER_IERR, "UE_SoC_IErr"},
        {0, "errors_core_ce", TYPE_CORE, CE_CORE_IERR, "CE_CPU_IError"},
        {0, "errors_mem_ce", TYPE_MEM, CE_MEM_IERR, "CE_Memory_IErr"},
        {0, "errors_pcie_ce", TYPE_PCIE, CE_PCIE_IERR, "CE_PCIE_IErr"},
        {0, "errors_other_ce", TYPE_OTHER, CE_OTHER_IERR, "CE_SoC_IErr"},
        {1, "errors_core_ce", TYPE_CORE, CE_CORE_IERR, "CE_CPU_IError"},
        {1, "errors_mem_ce", TYPE_MEM, CE_MEM_IERR, "CE_Memory_IErr"},
        {1, "errors_pcie_ce", TYPE_PCIE, CE_PCIE_IERR, "CE_PCIE_IErr"},
        {1, "errors_other_ce", TYPE_OTHER, CE_OTHER_IERR, "CE_SoC_IErr"},
    };
    const static constexpr u_int8_t NUMBER_OF_ERRORS    =
            sizeof(errorTypeTable) / sizeof(ErrorData);

    static int logErrorToIpmiSEL(ErrorData data, ErrorFields eFields)
    {
        std::vector<uint8_t> eventData(
                ampere::sel::SEL_OEM_DATA_MAX_SIZE, 0xFF);

        eventData[0] = 0x3A;
        eventData[1] = 0xCD;
        eventData[2] = 0x00;
        eventData[3] = data.errType;
        eventData[4] = data.errNum;
        eventData[5] = eFields.errType;
        eventData[6] = eFields.subType;
        eventData[7] = (eFields.instance & 0xff00) >> 8;
        eventData[8] = (eFields.instance & 0xff);

        ampere::sel::addSelOem("OEM RAS error:", eventData);

        return 1;
    }

    static int parseAndLogErrors(ErrorData data, std::string errLine)
    {
        ErrorFields errFields;
        std::vector<std::string> result;

        errLine.erase(std::remove(errLine.begin(), errLine.end(), '\n'),
            errLine.end());
        boost::split(result, errLine, boost::is_any_of(" "));
        if (result.size() < 5)
            return 0;
        errFields.errType = ampere::utils::parseHexStrToUInt8(result[0]);
        errFields.subType = ampere::utils::parseHexStrToUInt8(result[1]);
        errFields.instance = ampere::utils::parseHexStrToUInt16(result[2]);
        errFields.status = ampere::utils::parseHexStrToUInt32(result[3]);
        errFields.address = ampere::utils::parseHexStrToUInt64(result[4]);

        /* Add Ipmi SEL log*/
        logErrorToIpmiSEL(data, errFields);

        return 1;
    }

    static int logErrors(ErrorData data, const char *fileName) {
        FILE *fp;
        char* line = NULL;

        /* Read system file */
        fp = fopen(fileName, "r");
        if (!fp)
            return 0;

        size_t len = 0;
        while ((getline(&line, &len, fp)) != -1) {
            parseAndLogErrors(data, line);
        }

        fclose(fp);
        if (line)
            free(line);
        return 1;
    }

    static int getErrors()
    {
        std::string filePath;
        int index = 0;
        bool c0ntinue = true;

        index = 0;
        while(c0ntinue) {
            ErrorData data = errorTypeTable[index];
            filePath = ampere::utils::getAbsolutePath(
                    data.socket, data.label);
            logErrors(data, filePath.c_str());

            index++;
            if (index >= NUMBER_OF_ERRORS) {
                index = 0;
                sleep(1);
            }
        }
        return 1;
    }

} /* namespace ras */
} /* namespace ampere */

int main(int argc, char** argv)
{
    int ret;
    log<level::INFO>("Starting xyz.openbmc_project.AmpRas.service");

    /* Init Dbus connect for SEL log */
    ampere::sel::initSelUtil();

    ret = ampere::utils::initHwmonRootPath();
    if (!ret) {
        log<level::ERR>("Failed to get Root Path of SMPro Hwmon\n");
        return 0;
    }

    ret = ampere::ras::getErrors();
    if (!ret) {
        log<level::ERR>("Failed to retrieve errors from SMPro\n");
        return 0;
    }

    return 0;
}
