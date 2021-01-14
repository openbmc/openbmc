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

#include <systemd/sd-journal.h>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/predicate.hpp>
#include <boost/algorithm/string/replace.hpp>
#include <boost/asio.hpp>
#include <boost/asio/posix/stream_descriptor.hpp>
#include <boost/container/flat_map.hpp>
#include <boost/container/flat_set.hpp>
#include <gpiod.hpp>
#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/elog.hpp>
#include <phosphor-logging/log.hpp>
#include <sdbusplus/asio/object_server.hpp>
#include <sdbusplus/bus.hpp>
#include <sdbusplus/message.hpp>

#include <filesystem>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <sstream>

static gpiod::line BMC_SELECT_EEPROM;
static gpiod::line S0_SCP_AUTH_FAIL_L;
static boost::asio::io_service io;

static boost::asio::posix::stream_descriptor s0ScpAuthFailEvent(io);
static std::shared_ptr<sdbusplus::asio::connection> conn;
static bool released_gpio = false;

static bool requestGPIOInput(const std::string& name, gpiod::line& gpioLine)
{
    // Find the GPIO line
    gpioLine = gpiod::find_line(name);
    if (!gpioLine)
    {
        std::cerr << "Failed to find the " << name << " line.\n";
        return false;
    }

    // Request GPIO input
    try
    {
        gpioLine.request({__FUNCTION__, gpiod::line_request::DIRECTION_INPUT});
    }
    catch (std::exception&)
    {
        std::cerr << "Failed to request " << name << " input\n";
        return false;
    }

    return true;
}

static bool requestGPIOOutput(const std::string& name, const int value,
                              gpiod::line& gpioLine)
{
    // Find the GPIO line
    gpioLine = gpiod::find_line(name);
    if (!gpioLine)
    {
        std::cerr << "Failed to find the " << name << " line.\n";
        return false;
    }

    // Request GPIO output to specified value
    try
    {
        gpioLine.request({__FUNCTION__, gpiod::line_request::DIRECTION_OUTPUT},
                         value);
        gpioLine.set_value(value);
    }
    catch (std::exception&)
    {
        std::cerr << "Failed to request " << name << " output\n";
        return false;
    }

    return true;
}

static void requestGPIOs()
{
    try
    {
        released_gpio = false;
        requestGPIOOutput("BMC_SELECT_EEPROM", 1, BMC_SELECT_EEPROM);
        sleep(5);
    }
    catch (std::exception& e)
    {
        std::string message = "Cannot get the GPIOs, exit ...";
        sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i", LOG_ERR,
                        NULL);
    }
}

static void releaseGPIOs()
{
    try
    {
        released_gpio = true;
        BMC_SELECT_EEPROM.release();
    }
    catch (std::exception& e)
    {
        std::string message = "Cannot release the GPIOs, exit ...";
        sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i", LOG_ERR,
                        NULL);
    }
}

static bool requestGPIOEvents(
    const std::string& name, const std::function<void()>& handler,
    gpiod::line& gpioLine,
    boost::asio::posix::stream_descriptor& gpioEventDescriptor)
{
    // Find the GPIO line
    gpioLine = gpiod::find_line(name);
    if (!gpioLine)
    {
        std::cerr << "Failed to find the " << name << " line\n";
        return false;
    }

    try
    {
        gpioLine.request(
            {"ampere-scp-failover", gpiod::line_request::EVENT_BOTH_EDGES});
    }
    catch (std::exception&)
    {
        std::cerr << "Failed to request events for " << name << "\n";
        return false;
    }

    int gpioLineFd = gpioLine.event_get_fd();
    if (gpioLineFd < 0)
    {
        std::cerr << "Failed to get " << name << " fd\n";
        return false;
    }

    gpioEventDescriptor.assign(gpioLineFd);

    gpioEventDescriptor.async_wait(
        boost::asio::posix::stream_descriptor::wait_read,
        [&name, handler](const boost::system::error_code ec) {
            if (ec)
            {
                std::cerr << name << " fd handler error: " << ec.message()
                          << "\n";
                return;
            }
            handler();
        });
    return true;
}

static void doForceReset()
{
    std::string command =
        "xyz.openbmc_project.State.Host.Transition.ForceWarmReboot";
    conn->async_method_call(
        [](const boost::system::error_code ec) {
            if (ec)
            {
                std::cerr << "[Set] Bad D-Bus request error in doForceReset: "
                          << ec;
                return;
            }
        },
        "xyz.openbmc_project.State.Host", "/xyz/openbmc_project/state/host0",
        "org.freedesktop.DBus.Properties", "Set",
        "xyz.openbmc_project.State.Host", "RequestedHostTransition",
        std::variant<std::string>{command});
}

static void handleSCPAuthFail()
{
    int i2c_backup_sel_value = -1;
    std::string message = "";
    std::stringstream stream;

    gpiod::line_event gpioLineEvent = S0_SCP_AUTH_FAIL_L.event_read();
    bool authFail = gpioLineEvent.event_type == gpiod::line_event::FALLING_EDGE;
    if (authFail)
    {
        try
        {
            if (released_gpio)
            {
                requestGPIOs();
            }
            // Get the GPIO values
            i2c_backup_sel_value = BMC_SELECT_EEPROM.get_value();

            stream << "scp auth failure signal: "
                   << (i2c_backup_sel_value ? "boot main eeprom"
                                            : "boot failover eeprom")
                   << "(" << i2c_backup_sel_value << ")" << std::endl;

            message = stream.str();
            sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i",
                            LOG_ERR, NULL);
        }
        catch (std::exception& e)
        {
            message = "Cannot get the GPIOs, exit ...";
            sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i",
                            LOG_ERR, NULL);
        }

        // 0 = failover, 1 = main
        if (!i2c_backup_sel_value)
        {
            std::string msg = "scp authentication failure detected, failover "
                              "eeprom boots fail";
            sd_journal_send(
                "MESSAGE=%s", msg.c_str(), "PRIORITY=%i", LOG_ERR,
                "REDFISH_MESSAGE_ID=%s", "OpenBMC.0.1.AmpereCritical",
                "REDFISH_MESSAGE_ARGS=%s, %s", "SCP", msg.c_str(), NULL);
        }
        else
        {
            try
            {
                // switch to failover
                BMC_SELECT_EEPROM.set_value(0);
                sleep(1);

                // just add systemd log, no SEL or Redfish log
                message = "scp authentication failure detected, switching to "
                          "failover eeprom";
                sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i",
                                LOG_ERR, NULL);

                // reset the host
                doForceReset();
            }
            catch (std::exception& e)
            {
                message = "auth failure detected, but action failed";
                sd_journal_send("MESSAGE=%s", message.c_str(), "PRIORITY=%i",
                                LOG_ERR, NULL);
            }
        }
    }

    s0ScpAuthFailEvent.async_wait(
        boost::asio::posix::stream_descriptor::wait_read,
        [](const boost::system::error_code ec) {
            if (ec)
            {
                std::cerr << "SCP failover handler error: " << ec.message()
                          << "\n";
                return;
            }
            handleSCPAuthFail();
        });
}

inline static sdbusplus::bus::match::match
    startPowerGoodMonitor(std::shared_ptr<sdbusplus::asio::connection> conn)
{
    auto startEventMatcherCallback = [](sdbusplus::message::message& msg) {
        boost::container::flat_map<std::string, std::variant<int>>
            propertiesChanged;
        std::string interfaceName;

        msg.read(interfaceName, propertiesChanged);
        if (propertiesChanged.empty())
        {
            return;
        }

        std::string event = propertiesChanged.begin()->first;
        if (event.empty())
        {
            return;
        }

        if (event == "pgood")
        {
            int value = std::get<int>(propertiesChanged.begin()->second);
            // DC power is off
            if (value == 0)
            {
                try
                {
                    // set back to boot from main
                    BMC_SELECT_EEPROM.set_value(1);
                    sleep(1);
                    releaseGPIOs();
                }
                catch (std::exception& e)
                {}
            }
        }
    };

    sdbusplus::bus::match::match startEventMatcher(
        static_cast<sdbusplus::bus::bus&>(*conn),
        "type='signal',interface='org.freedesktop.DBus.Properties',"
        "member='PropertiesChanged',path='/org/openbmc/control/power0',"
        "arg0namespace='org.openbmc.control.Power'",
        std::move(startEventMatcherCallback));

    return startEventMatcher;
}

int main(int argc, char* argv[])
{
    conn = std::make_shared<sdbusplus::asio::connection>(io);
    sdbusplus::bus::match::match powerGoodMonitor = startPowerGoodMonitor(conn);

    requestGPIOs();
    requestGPIOEvents("S0_SCP_AUTH_FAIL_L", handleSCPAuthFail,
                      S0_SCP_AUTH_FAIL_L, s0ScpAuthFailEvent);

    io.run();
    return 0;
}
