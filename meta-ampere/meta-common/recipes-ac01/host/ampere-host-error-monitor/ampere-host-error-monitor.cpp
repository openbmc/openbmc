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
#include "internalErrors.hpp"
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
    const static constexpr u_int8_t TYPE_SMPM       = 0xCA;
    const static constexpr u_int8_t CE_CORE_IERR    = 139;
    const static constexpr u_int8_t UE_CORE_IERR    = 140;
    const static constexpr u_int8_t CE_OTHER_IERR   = 141;
    const static constexpr u_int8_t UE_OTHER_IERR   = 142;
    const static constexpr u_int8_t CE_MEM_IERR     = 151;
    const static constexpr u_int8_t UE_MEM_IERR     = 168;
    const static constexpr u_int8_t CE_PCIE_IERR    = 191;
    const static constexpr u_int8_t UE_PCIE_IERR    = 202;
    const static constexpr u_int8_t SMPRO_IERR     = 147;
    const static constexpr u_int8_t PMPRO_IERR     = 148;
    /* Direction of RAS Internal errors */
    const static constexpr u_int8_t  DIR_ENTER      = 0;
    const static constexpr u_int8_t  DIR_EXIT       = 1;
    /* Sub types of RAS Internal errors */
    const static constexpr u_int8_t  SMPMPRO_WARNING       = 1;
    const static constexpr u_int8_t  SMPMPRO_ERROR         = 2;
    const static constexpr u_int8_t  SMPMPRO_ERROR_DATA    = 4;
    /* Type of RAS Internal errors */
    const static constexpr u_int8_t  SMPRO_IERR_TYPE        = 0;
    const static constexpr u_int8_t  PMPRO_IERR_TYPE        = 1;

    const static constexpr u_int8_t IERR_SENSOR_SPECIFIC     = 0x71;

    struct ErrorFields {
        u_int8_t errType;
        u_int8_t subType;
        u_int16_t instance;
        u_int32_t status;
        u_int64_t address;
    };

    struct InternalFields {
        u_int8_t errType;
        u_int8_t subType;
        u_int8_t imageCode;
        u_int8_t dir;
        u_int8_t location;
        u_int16_t errCode;
        u_int32_t data;
    };

    struct ErrorData {
        u_int16_t socket;
        u_int8_t intErrorType;
        const char* label;
        u_int8_t errType;
        u_int8_t errNum;
        const char* errName;
        const char* redFishMsgID;
    };
    /* Error type index of RAS Errors */
    enum ErrorTypes{
        errors_core_ue,
        errors_mem_ue,
        errors_pcie_ue,
        errors_other_ue,
        errors_core_ce,
        errors_mem_ce,
        errors_pcie_ce,
        errors_other_ce,
        errors_smpro,
        errors_pmpro
    };

    ErrorData errorTypeTable[] = {
        {0, errors_core_ue, "errors_core_ue", TYPE_CORE, UE_CORE_IERR,
            "UE_CPU_IError", "CPUError"},
        {0, errors_mem_ue, "errors_mem_ue", TYPE_MEM, UE_MEM_IERR,
            "UE_Memory_IErr", "MemoryECCUncorrectable"},
        {0, errors_pcie_ue, "errors_pcie_ue", TYPE_PCIE, UE_PCIE_IERR,
            "UE_PCIE_IErr", "PCIeFatalUncorrectableInternal"},
        {0, errors_other_ue, "errors_other_ue", TYPE_OTHER, UE_OTHER_IERR,
            "UE_SoC_IErr", "AmpereCritical"},
        {1, errors_core_ue, "errors_core_ue", TYPE_CORE, UE_CORE_IERR,
            "UE_CPU_IError", "CPUError"},
        {1, errors_mem_ue, "errors_mem_ue", TYPE_MEM, UE_MEM_IERR,
            "UE_Memory_IErr", "MemoryECCUncorrectable"},
        {1, errors_pcie_ue, "errors_pcie_ue", TYPE_PCIE, UE_PCIE_IERR,
            "UE_PCIE_IErr", "PCIeFatalUncorrectableInternal"},
        {1, errors_other_ue, "errors_other_ue", TYPE_OTHER, UE_OTHER_IERR,
            "UE_SoC_IErr", "AmpereCritical"},
        {0, errors_core_ce, "errors_core_ce", TYPE_CORE, CE_CORE_IERR,
            "CE_CPU_IError", "CPUError"},
        {0, errors_mem_ce, "errors_mem_ce", TYPE_MEM, CE_MEM_IERR,
            "CE_Memory_IErr", "MemoryECCCorrectable"},
        {0, errors_pcie_ce, "errors_pcie_ce", TYPE_PCIE, CE_PCIE_IERR,
            "CE_PCIE_IErr", "PCIeFatalECRCError"},
        {0, errors_other_ce, "errors_other_ce", TYPE_OTHER, CE_OTHER_IERR,
            "CE_SoC_IErr", "AmpereCritical"},
        {1, errors_core_ce, "errors_core_ce", TYPE_CORE, CE_CORE_IERR,
            "CE_CPU_IError", "CPUError"},
        {1, errors_mem_ce, "errors_mem_ce", TYPE_MEM, CE_MEM_IERR,
            "CE_Memory_IErr", "MemoryECCCorrectable"},
        {1, errors_pcie_ce, "errors_pcie_ce", TYPE_PCIE, CE_PCIE_IERR,
            "CE_PCIE_IErr", "PCIeFatalECRCError"},
        {1, errors_other_ce, "errors_other_ce", TYPE_OTHER, CE_OTHER_IERR,
            "CE_SoC_IErr", "AmpereCritical"},
        {0, errors_smpro, "errors_smpro", TYPE_SMPM, SMPRO_IERR,
            "SMPRO_IErr", "AmpereCritical"},
        {0, errors_pmpro, "errors_pmpro", TYPE_SMPM, PMPRO_IERR,
            "PMPRO_IErr", "AmpereCritical"},
        {1, errors_smpro, "errors_smpro", TYPE_SMPM, SMPRO_IERR,
            "SMPRO_IErr", "AmpereCritical"},
        {1, errors_pmpro, "errors_pmpro", TYPE_SMPM, PMPRO_IERR,
            "PMPRO_IErr", "AmpereCritical"},
    };

    const static constexpr u_int8_t NUMBER_OF_ERRORS    =
            sizeof(errorTypeTable) / sizeof(ErrorData);

    struct ErrorInfo {
        u_int8_t errType;
        u_int8_t subType;
        u_int8_t numPars;
        const char* errName;
        const char* errMsgFormat;
    };
    
    std::map<u_int16_t, ErrorInfo> mapOfOccur = {
        {0x0000, {0, 0, 2, "CPM Snoop-Logic", "Socket%s CPM%s"}},
        {0x0001, {0, 1, 2, "CPM Core 0", "Socket%s CPM%s"}},
        {0x0002, {0, 2, 2, "CPM Core 1", "Socket%s CPM%s"}},
        {0x0101, {1, 1, 2, "MCU ERR Record 1 (DRAM CE)", "Socket%s MCU%s"}},
        {0x0102, {1, 2, 2, "MCU ERR Record 2 (DRAM UE)", "Socket%s MCU%s"}},
        {0x0103, {1, 3, 2, "MCU ERR Record 3 (CHI Fault)", "Socket%s MCU%s"}},
        {0x0104, {1, 4, 2, "MCU ERR Record 4 (SRAM CE)", "Socket%s MCU%s"}},
        {0x0105, {1, 5, 2, "MCU ERR 5 (SRAM UE)", "Socket%s MCU%s"}},
        {0x0106, {1, 6, 2, "MCU ERR 6 (DMC recovery)", "Socket%s MCU%s"}},
        {0x0107, {1, 7, 2, "MCU Link ERR", "Socket%s MCU%s"}},
        {0x0200, {2, 0, 2, "Mesh XP", "Socket%s instance:%s"}},
        {0x0201, {2, 1, 2, "Mesh HNI", "Socket%s instance:%s"}},
        {0x0202, {2, 2, 2, "Mesh HNF", "Socket%s instance:%s"}},
        {0x0204, {2, 4, 2, "Mesh CXG", "Socket%s instance:%s"}},
        {0x0300, {3, 0, 2, "2P CCIX ERR", "Socket%s Link%s"}},
        {0x0400, {4, 0, 2, "2P ALI ERR", "Socket%s Link%s"}},
        {0x0500, {5, 0, 1, "GIC ERR 0", "Socket%s"}},
        {0x0501, {5, 1, 1, "GIC ERR 1", "Socket%s"}},
        {0x0502, {5, 2, 1, "GIC ERR 2", "Socket%s"}},
        {0x0503, {5, 3, 1, "GIC ERR 3", "Socket%s"}},
        {0x0504, {5, 4, 1, "GIC ERR 4", "Socket%s"}},
        {0x0505, {5, 5, 1, "GIC ERR 5", "Socket%s"}},
        {0x0506, {5, 6, 1, "GIC ERR 6", "Socket%s"}},
        {0x0507, {5, 7, 1, "GIC ERR 7", "Socket%s"}},
        {0x0508, {5, 8, 1, "GIC ERR 8", "Socket%s"}},
        {0x0509, {5, 9, 1, "GIC ERR 9", "Socket%s"}},
        {0x050a, {5, 10, 1, "GIC ERR 10", "Socket%s"}},
        {0x050b, {5, 11, 1, "GIC ERR 11", "Socket%s"}},
        {0x050c, {5, 12, 1, "GIC ERR 12", "Socket%s"}},
        {0x0600, {6, 0, 2, "SMMU TBU0", "Socket%s Root complex:%s"}},
        {0x0601, {6, 1, 2, "SMMU TBU1", "Socket%s Root complex:%s"}},
        {0x0602, {6, 2, 2, "SMMU TBU2", "Socket%s Root complex:%s"}},
        {0x0603, {6, 3, 2, "SMMU TBU3", "Socket%s Root complex:%s"}},
        {0x0604, {6, 4, 2, "SMMU TBU4", "Socket%s Root complex:%s"}},
        {0x0605, {6, 5, 2, "SMMU TBU5", "Socket%s Root complex:%s"}},
        {0x0606, {6, 6, 2, "SMMU TBU6", "Socket%s Root complex:%s"}},
        {0x0607, {6, 7, 2, "SMMU TBU7", "Socket%s Root complex:%s"}},
        {0x0608, {6, 8, 2, "SMMU TBU8", "Socket%s Root complex:%s"}},
        {0x0609, {6, 9, 2, "SMMU TBU9", "Socket%s Root complex:%s"}},
        {0x0664, {6, 100, 2, "SMMU TCU", "Socket%s Root complex:%s"}},
        {0x0700, {7, 0, 2, "PCIe AER Root Port", "Socket%s Root complex:%s"}},
        {0x0701, {7, 1, 2, "PCIe AER Device", "Socket%s Root complex:%s"}},
        {0x0800, {8, 0, 2, "PCIe HB RCA", "Socket%s Root complex:%s"}},
        {0x0801, {8, 1, 2, "PCIe HB RCA", "Socket%s Root complex:%s"}},
        {0x0808, {8, 8, 2, "PCIe RASDP Error ", "Socket%s Root complex:%s"}},
        {0x0900, {9, 0, 1, "OCM ERR 0 (ECC Fault)", "Socket%s"}},
        {0x0901, {9, 1, 1, "OCM ERR 1 (ERR Recovery)", "Socket%s"}},
        {0x0902, {9, 2, 1, "OCM ERR 2 (Data Poisoned)", "Socket%s"}},
        {0x0a00, {10, 0, 1, "SMpro ERR 0 (ECC Fault)", "Socket%s"}},
        {0x0a01, {10, 1, 1, "SMpro ERR 1 (ERR Recovery)", "Socket%s"}},
        {0x0a02, {10, 2, 1, "SMpro MPA_ERR", "Socket%s"}},
        {0x0b00, {11, 0, 1, "PMpro ERR 0 (ECC Fault)", "Socket%s"}},
        {0x0b01, {11, 1, 1, "PMpro ERR 1 (ERR Recovery)", "Socket%s"}},
        {0x0b02, {11, 2, 1, "PMpro MPA_ERR", "Socket%s"}},
        {0x0c00, {12, 0, 1, "ATF firmware EL3", "Socket%s"}},
        {0x0c01, {12, 1, 1, "ATF firmware SPM", "Socket%s"}},
        {0x0c02, {12, 2, 1, "ATF firmware Secure Partition ", "Socket%s"}},
        {0x0d00, {13, 0, 1, "SMpro firmware RAS_MSG_ERR", "Socket%s"}},
        {0x0e00, {14, 0, 1, "PMpro firmware RAS_MSG_ERR", "Socket%s"}},
        {0x3f00, {63, 0, 1, "BERT Default", "Socket%s"}},
        {0x3f01, {63, 1, 1, "BERT Watchdog", "Socket%s"}},
        {0x3f02, {63, 2, 1, "BERT ATF Fatal", "Socket%s"}},
        {0x3f03, {63, 3, 1, "BERT SMpro Fatal", "Socket%s"}},
        {0x3f04, {63, 4, 1, "BERT PMpro Fatal", "Socket%s"}},
    };

    const static constexpr u_int16_t MCU_ERR_1_TYPE    = 0x0101;
    const static constexpr u_int16_t MCU_ERR_2_TYPE    = 0x0102;

    static int logInternalErrorToIpmiSEL(ErrorData data,
                                        InternalFields eFields)
    {
        std::vector<uint8_t> eventData(
            ampere::sel::SEL_OEM_DATA_MAX_SIZE, 0xFF);

        eventData[0] = 0x3A;
        eventData[1] = 0xCD;
        eventData[2] = 0x00;
        eventData[3] = data.errType;
        eventData[4] = data.errNum;
        eventData[5] = (eFields.dir << 7) | IERR_SENSOR_SPECIFIC;
        eventData[6] = ((data.socket & 0x1) << 7) |
            ((eFields.subType & 0x7) << 4) | (eFields.imageCode & 0xf);
        eventData[7] = eFields.location & 0xff;
        eventData[8] = eFields.errCode & 0xff;
        eventData[9] = (eFields.errCode & 0xff00) >> 8;
        eventData[10] = eFields.data & 0xff;
        eventData[11] = (eFields.data & 0xff00) >> 8;

        ampere::sel::addSelOem("OEM RAS error:", eventData);

        return 1;
    }

    static int logInternalErrorToRedfish(ErrorData data,
                                        InternalFields eFields)
    {
        char redfishMsgID[MAX_MSG_LEN] = {'\0'};
        char redfishMsg[MAX_MSG_LEN] = {'\0'};
        char sLocation[MAX_MSG_LEN] = "Unknown location";
        char sErrorCode[MAX_MSG_LEN] = "Unknown Error";
        char sImage[MAX_MSG_LEN] = "Unknown Image";
        char sDir[MAX_MSG_LEN] = "Unknown Action";
        char redfishComp[MAX_MSG_LEN] = {'\0'};

        if (eFields.location < ampere::internalErrors::NUM_LOCAL_CODES)
            snprintf(sLocation, MAX_MSG_LEN,
                ampere::internalErrors::localCodes[eFields.location]);

        if (eFields.imageCode < ampere::internalErrors::NUM_IMAGE_CODES)
            snprintf(sImage, MAX_MSG_LEN,
                ampere::internalErrors::imageCodes[eFields.imageCode]);

        if (eFields.errCode < ampere::internalErrors::NUM_ERROR_CODES)
            snprintf(sErrorCode, MAX_MSG_LEN,
                ampere::internalErrors::errorCodes[eFields.errCode]\
                    .description);

        if (eFields.dir < ampere::internalErrors::NUM_DIRS)
            snprintf(sDir, MAX_MSG_LEN,
                ampere::internalErrors::directions[eFields.dir]);

        snprintf(redfishComp, MAX_MSG_LEN, "S%d_%s: %s %s %s with",
            data.socket, data.errName, sImage, sDir, sLocation);

        if (eFields.subType == SMPMPRO_WARNING)
        {
            snprintf(redfishMsgID, MAX_MSG_LEN,
                "OpenBMC.0.1.%s.Warning", data.redFishMsgID);
            snprintf(redfishMsg, MAX_MSG_LEN, "Warning %s.", sErrorCode);
        }
        else
        {
           snprintf(redfishMsgID, MAX_MSG_LEN,
                "OpenBMC.0.1.%s.Critical", data.redFishMsgID);
            if (eFields.subType == SMPMPRO_ERROR)
                snprintf(redfishMsg, MAX_MSG_LEN, "Error %s.", sErrorCode);
            else
                snprintf(redfishMsg, MAX_MSG_LEN, "Error %s, data 0x%08x.",
                    sErrorCode, eFields.data);
        }

        if (data.intErrorType == errors_smpro ||
                data.intErrorType == errors_pmpro) {
            sd_journal_send("REDFISH_MESSAGE_ID=%s", redfishMsgID,
                        "REDFISH_MESSAGE_ARGS=%s,%s", redfishComp,
                        redfishMsg, NULL);
        }
        return 1;
    }

    static int parseAndLogInternalErrors(ErrorData data, std::string errLine)
    {
        InternalFields errFields;
        std::vector<std::string> result;

        errLine.erase(std::remove(errLine.begin(), errLine.end(), '\n'),
            errLine.end());
        boost::split(result, errLine, boost::is_any_of(" "));
        if (result.size() < 6)
            return 0;

        if (data.intErrorType == errors_smpro)
            errFields.errType = SMPRO_IERR_TYPE;
        else
            errFields.errType = PMPRO_IERR_TYPE;

        errFields.subType = ampere::utils::parseHexStrToUInt8(result[0]);
        errFields.imageCode = ampere::utils::parseHexStrToUInt8(result[1]);
        errFields.dir = ampere::utils::parseHexStrToUInt8(result[2]);
        errFields.location = ampere::utils::parseHexStrToUInt8(result[3]);
        errFields.errCode = ampere::utils::parseHexStrToUInt16(result[4]);
        errFields.data = ampere::utils::parseHexStrToUInt32(result[5]);

        /* Add SEL log */
        logInternalErrorToIpmiSEL(data, errFields);

        /* Add Redfish log */
        logInternalErrorToRedfish(data, errFields);

        return 1;
    }

    static int logErrorToRedfish(ErrorData data, ErrorFields eFields)
    {
        char redFishMsgID[MAX_MSG_LEN] = {'\0'};
        char redFishMsg[MAX_MSG_LEN] = {'\0'};
        char redFishComp[MAX_MSG_LEN] = {'\0'};
        u_int8_t socket = (eFields.instance & 0xc000) >> 14;
        u_int16_t inst_13_0 = eFields.instance & 0x3fff;
        u_int8_t apiIdx = data.intErrorType;
        u_int16_t temp;
        ErrorInfo eInfo;

        snprintf(redFishMsgID, MAX_MSG_LEN,
            "OpenBMC.0.1.%s.Critical", data.redFishMsgID);
        temp = (eFields.errType << 8) + eFields.subType;
        if (mapOfOccur.size() != 0 && mapOfOccur.count(temp) > 0) {
            eInfo = mapOfOccur[temp];
            char str1[4] = {'\0'};
            char str2[4] = {'\0'};
            snprintf(str1, 4, "%d", socket);
            snprintf(str2, 4, "%d", inst_13_0);

            if (eInfo.numPars == 1)
                snprintf(redFishMsg, MAX_MSG_LEN, eInfo.errMsgFormat, str1);
            else if (eInfo.numPars == 2)
                snprintf(redFishMsg, MAX_MSG_LEN, eInfo.errMsgFormat, str1,
                            str2);
            snprintf(redFishComp, MAX_MSG_LEN, "%s", eInfo.errName);
        }

        if (apiIdx == errors_core_ue || apiIdx == errors_core_ce) {
            char sTemp[MAX_MSG_LEN] = {'\0'};
            snprintf(sTemp, MAX_MSG_LEN, "%s: %s %s", data.errName,
                        redFishComp, redFishMsg);
            sd_journal_send("REDFISH_MESSAGE_ID=%s", redFishMsgID,
                        "REDFISH_MESSAGE_ARGS=%s", sTemp, NULL);
        } else if (apiIdx == errors_mem_ue || apiIdx == errors_mem_ce) {
            char dimCh[MAX_MSG_LEN] = {'\0'};
            snprintf(dimCh, MAX_MSG_LEN, "%x", (inst_13_0 & 0x7ff));
            /* Only detect DIMM Idx for MCU_ERROR_1 or MCU_ERROR_2 Type */
            if (temp == MCU_ERR_1_TYPE || temp == MCU_ERR_2_TYPE)
                sd_journal_send("REDFISH_MESSAGE_ID=%s", redFishMsgID,
                        "REDFISH_MESSAGE_ARGS=%d,%s,%d,%d", socket,
                        dimCh, (inst_13_0 & 0x3800) >> 11,
                        (eFields.address >> 20) & 0xF , NULL);
            else
                sd_journal_send("REDFISH_MESSAGE_ID=%s", redFishMsgID,
                        "REDFISH_MESSAGE_ARGS=%d,%s,%d,%d", socket,
                        dimCh, 0xff, 0xff, NULL);
            
        } else if (apiIdx == errors_pcie_ue || apiIdx == errors_pcie_ce) {
            sd_journal_send("REDFISH_MESSAGE_ID=%s", redFishMsgID,
                        "REDFISH_MESSAGE_ARGS=%d,%d,%d", socket,
                        inst_13_0, 0, NULL);
        } else if (apiIdx == errors_other_ue || apiIdx == errors_other_ce) {
            char comp[MAX_MSG_LEN] = {'\0'};
            snprintf(comp, MAX_MSG_LEN, "%s: %s", data.errName, redFishComp);
            sd_journal_send("REDFISH_MESSAGE_ID=%s", redFishMsgID,
                        "REDFISH_MESSAGE_ARGS=%s,%s", comp, redFishMsg, NULL);
        }
        return 1;
    }

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

        /* Add Redfish log */
        logErrorToRedfish(data, errFields);

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
            if (data.intErrorType == errors_smpro ||
                    data.intErrorType == errors_pmpro)
                parseAndLogInternalErrors(data, line);
            else
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
