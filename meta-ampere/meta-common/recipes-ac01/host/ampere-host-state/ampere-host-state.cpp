/*
// Copyright 2020 Ampere Computing LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/

#include <boost/algorithm/string.hpp>
#include <boost/asio/io_service.hpp>
#include <boost/container/flat_map.hpp>
#include <sdbusplus/asio/connection.hpp>
#include <sdbusplus/bus.hpp>
#include <sdbusplus/bus/match.hpp>
#include <sdbusplus/message.hpp>

#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/elog.hpp>
#include <phosphor-logging/log.hpp>

#include <filesystem>
#include <fstream>
#include <iostream>
#include <memory>
#include <regex>
#include <gpiod.hpp>

using namespace phosphor::logging;

namespace ampere
{
namespace host
{
/* const */
constexpr const char* profInterface = "org.freedesktop.DBus.Properties";
constexpr const char* getMethod = "Get";
const static constexpr char* chassisService =
                            "xyz.openbmc_project.State.Chassis";
const static constexpr char* chassisPath    =
                            "/xyz/openbmc_project/state/chassis0";
const static constexpr char* powerStateIntf =
                            "xyz.openbmc_project.State.Chassis";
const static constexpr char* powerState     = "CurrentPowerState";

const static constexpr char* hostService   = "xyz.openbmc_project.State.Host";
const static constexpr char* hostPath    = "/xyz/openbmc_project/state/host0";
const static constexpr char* hostStateIntf = "xyz.openbmc_project.State.Host";
const static constexpr char* hostStatePro  = "CurrentHostState";
const static constexpr char* HOST_STATE_RUNNING =
    "xyz.openbmc_project.State.Host.HostState.Running";
const static constexpr char* HOST_STATE_OFF =
    "xyz.openbmc_project.State.Host.HostState.Off";
const static constexpr char* HOST_STATE_QUIESCED =
    "xyz.openbmc_project.State.Host.HostState.Quiesced";
const static constexpr char* HOST_STATE_DIAG =
    "xyz.openbmc_project.State.Host.HostState.DiagnosticMode";

/* gpio lines define */
static gpiod::line S0_FW_BOOT_OK;

/* variables definition */
static std::string retPropState;

// connection to sdbus
static boost::asio::io_service io;
static std::shared_ptr<sdbusplus::asio::connection> conn;

static void setPropertyInString(
    std::shared_ptr<sdbusplus::asio::connection>& conn,
    std::string objService, std::string objPath, std::string intf,
    std::string prof, std::string state)
{
    conn->async_method_call(
        [](const boost::system::error_code ec) {
            if (ec)
                std::cerr << "Set: Dbus error: " << ec;
        },
        objService,
        objPath,
        profInterface, "Set",
        intf, prof,
        std::variant<std::string>(state));
}

static bool requestGPIOInput(const std::string &name, gpiod::line &gpioLine)
{
    // Find the GPIO line
    gpioLine = gpiod::find_line(name);
    if (!gpioLine) {
        log<level::ERR>("Failed to find the ", entry("%s line", name.c_str()));
        return false;
    }

    // Request GPIO input
    try {
        gpioLine.request({__FUNCTION__, gpiod::line_request::DIRECTION_INPUT});
    } catch (std::exception &) {
        log<level::ERR>("Failed to request ", entry("%s input", name.c_str()));
        return false;
    }

    return true;
}

static void updateHostState(
    std::shared_ptr<sdbusplus::asio::connection>& conn,
    int state)
{
    char buff[128];
    if (state) {
        sprintf(buff, "Updating %s to %s\n", hostStatePro, HOST_STATE_RUNNING);
        log<level::INFO>(buff);
        setPropertyInString(conn, hostService, hostPath,
            hostStateIntf, hostStatePro, HOST_STATE_RUNNING);
    }
    else {
        sprintf(buff, "Updating %s to %s\n", hostStatePro, HOST_STATE_OFF);
        log<level::INFO>(buff);
        setPropertyInString(conn, hostService, hostPath,
            hostStateIntf, hostStatePro, HOST_STATE_OFF);
    }
    return;
}

static void hostStateChangeCheckFwBootOk(
    std::shared_ptr<sdbusplus::asio::connection>& conn, int isHostOn)
{
next:
    try
    {
        /* Request S0_FW_BOOT_OK GPIO */
        requestGPIOInput("S0_FW_BOOT_OK", S0_FW_BOOT_OK);
        int s0_fw_boot_ok = (bool)S0_FW_BOOT_OK.get_value();
        /*
        * host state change, if not match with FW_BOOT_OK
        * rechange and exit.
        * phosphor-state-manager will confirm about changing
        * if the update state is the same with current state in dbus.
        * It will bypass.
        */
        if (isHostOn != s0_fw_boot_ok)
            updateHostState(conn, s0_fw_boot_ok);

        /* Release requested GPIOs */
        S0_FW_BOOT_OK.release();
    }
    catch (std::exception &e)
    {
        /* retry if failed to request GPIO */
        sleep(10);
        log<level::ERR>("Exception when read gpio\n");
        goto next;
    }
    return;
}

static void powerOnCheckFwBootOk(
    std::shared_ptr<sdbusplus::asio::connection>& conn, int isPowerOn)
{
    int s0_fw_boot_ok = -1;
    int last_fw_boot_state = -1;
    bool contCheck = true;
    char buff[128];
    int retry = 0;
    /*
     * After PowerOn, verify S0_FW_BOOT_OK in 10 seconds
     * If this pin still low, set CurrentHostState to Off
     * Else set to Running.
     */
    while (contCheck)
    {
        contCheck = true;
        try
        {
            /* Request S0_FW_BOOT_OK GPIO */
            requestGPIOInput("S0_FW_BOOT_OK", S0_FW_BOOT_OK);
            s0_fw_boot_ok = S0_FW_BOOT_OK.get_value();
            /*
             * the last state not match with FW_BOOT_OK state
             */
            if (s0_fw_boot_ok != last_fw_boot_state) {
                updateHostState(conn, s0_fw_boot_ok);
                last_fw_boot_state = s0_fw_boot_ok;
            }
            /* power on and host is on */
            if (s0_fw_boot_ok)
                contCheck = false;
            else {
                retry ++;
                if (retry == 5)
                    contCheck = false;
            }

            /* Release requested GPIOs */
            S0_FW_BOOT_OK.release();
            if (contCheck)
                sleep(2);

        }
        catch (std::exception &e)
        {
            log<level::ERR>("Exception when read gpio\n");
            goto next;
        }
next:
        try
        {
            // Release requested GPIOs
            S0_FW_BOOT_OK.release();
        }
        catch (std::exception &e)
        {
            log<level::ERR>("Exeption when release gpio\n");
            // Do nothing
        }
        sleep(10);
    }
    return;
}

static std::unique_ptr<sdbusplus::bus::match::match> startPowerStateMonitor()
{
    return std::make_unique<sdbusplus::bus::match::match>(
        *conn,
        "type='signal',interface='" + std::string(profInterface)
            + "',path='" + std::string(chassisPath) + "',arg0='"
            + std::string(powerStateIntf) + "'",
        [](sdbusplus::message::message& message) {
            std::string objectName;
            boost::container::flat_map<std::string, std::variant<std::string>>
                values;
            message.read(objectName, values);
            auto findState = values.find(powerState);
            if (findState == values.end()) {
                return;
            }
            bool powerStatusOff = boost::ends_with(
                    std::get<std::string>(findState->second), "Off");
            /*
             * When power on the host phosphor-state-manager
             * will change CurrentPowerState to On when psgood is high.
             * When this state is changed. Start check FW_BOOT_OK.
             */
            if (!powerStatusOff)
                powerOnCheckFwBootOk(conn, !powerStatusOff);
        });
}

static std::unique_ptr<sdbusplus::bus::match::match> startHostStateMonitor()
{
    return std::make_unique<sdbusplus::bus::match::match>(
        *conn,
        "type='signal',interface='" + std::string(profInterface)
            + "',path='" + std::string(hostPath) + "',arg0='"
            + std::string(hostStateIntf) + "'",
        [](sdbusplus::message::message& message) {
            std::string objectName;
            boost::container::flat_map<std::string, std::variant<std::string>>
                values;
            message.read(objectName, values);
            auto findState = values.find(hostStatePro);
            if (findState == values.end()) {
                return;
            }
            bool powerStatusOff = boost::ends_with(
                    std::get<std::string>(findState->second), "Off");
            /*
             * Compare CurrentHostState with FW_BOOT_OK when CurrentHostState
             * is changed.
             * It can cause phosphor-state-manager change state or User call 
             * "busctrl set-property ..." to change state.
             * If the host is on, but the user try to set property to Off,
             * force CurrentHostState to correct state base on S0_FW_BOOT_OK.
             */
            hostStateChangeCheckFwBootOk(conn, !powerStatusOff);
        });
}

} // namespace host
} // namespace ampere

int main(int argc, char** argv)
{
    // Initialize dbus connection
    boost::asio::io_service io;
    ampere::host::conn =
      std::make_shared<sdbusplus::asio::connection>(ampere::host::io);
    log<level::INFO>("Starting xyz.openbmc_project.Ampere.hostStateMonitor");
    ampere::host::conn->request_name(
            "xyz.openbmc_project.Ampere.hostStateMonitor");

    std::vector<std::unique_ptr<sdbusplus::bus::match::match>> matches;
    // Start tracking host state
    std::unique_ptr<sdbusplus::bus::match::match> powerMonitor =
        ampere::host::startPowerStateMonitor();
    matches.emplace_back(std::move(powerMonitor));
    //Start tracking power state
    std::unique_ptr<sdbusplus::bus::match::match> hostMonitor =
        ampere::host::startHostStateMonitor();
    matches.emplace_back(std::move(hostMonitor));

    /*
     * Synchronize CurrentHostState base on S0_BOOT_FW_OK when start
     */
    ampere::host::hostStateChangeCheckFwBootOk(ampere::host::conn, -1);

    /* wait for the signal */
    ampere::host::io.run();
    return 0;
}