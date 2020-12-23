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

inline static sdbusplus::bus::match::match
    startHostStateMonitor(std::shared_ptr<sdbusplus::asio::connection> conn)
{
    auto startEventMatcherCallback = [](sdbusplus::message::message& msg) {
        boost::container::flat_map<std::string, std::variant<std::string>>
            propertiesChanged;
        std::string interfaceName;

        msg.read(interfaceName, propertiesChanged);
        if (propertiesChanged.empty())
        {
            return;
        }

        std::string event = propertiesChanged.begin()->first;
        auto variant =
            std::get_if<std::string>(&propertiesChanged.begin()->second);

        if (event.empty() || variant == nullptr)
        {
            return;
        }

        if (event == "CurrentHostState")
        {
            if (*variant == "xyz.openbmc_project.State.Host.HostState.Off")
            {
                std::string message("Host state is off");
                std::string redfishMsgId("OpenBMC.0.1.AmpereWarning");

                sd_journal_send("MESSAGE=%s", message.c_str(),
                                "REDFISH_MESSAGE_ID=%s", redfishMsgId.c_str(),
                                "REDFISH_MESSAGE_ARGS=%s,%s",
                                "Host", "Host state is off", NULL);
            }
            else if (*variant ==
                     "xyz.openbmc_project.State.Host.HostState.Running")
            {
                std::string message("Host state is on");
                std::string redfishMsgId("OpenBMC.0.1.AmpereWarning");

                sd_journal_send("MESSAGE=%s", message.c_str(),
                                "REDFISH_MESSAGE_ID=%s", redfishMsgId.c_str(),
                                "REDFISH_MESSAGE_ARGS=%s,%s",
                                "Host", "Host state is on", NULL);
            }
        }
    };

    sdbusplus::bus::match::match startEventMatcher(
        static_cast<sdbusplus::bus::bus&>(*conn),
        "type='signal',interface='org.freedesktop.DBus.Properties',member='"
        "PropertiesChanged',arg0namespace='xyz.openbmc_project.State.Host'",
        std::move(startEventMatcherCallback));

    return startEventMatcher;
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
            if (value == 0)
            {
                std::string message("Host system DC power is off");
                std::string redfishMsgId("OpenBMC.0.1.DCPowerOff");

                sd_journal_send("MESSAGE=%s", message.c_str(),
                                "REDFISH_MESSAGE_ID=%s", redfishMsgId.c_str(),
                                NULL);
            }
            else if (value == 1)
            {
                std::string message("Host system DC power is on");
                std::string redfishMsgId("OpenBMC.0.1.DCPowerOn");

                sd_journal_send("MESSAGE=%s", message.c_str(),
                                "REDFISH_MESSAGE_ID=%s", redfishMsgId.c_str(),
                                NULL);
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
    boost::asio::io_service io;

    auto conn = std::make_shared<sdbusplus::asio::connection>(io);
    auto server = sdbusplus::asio::object_server(conn);

    sdbusplus::bus::match::match hostStateMonitor = startHostStateMonitor(conn);
    sdbusplus::bus::match::match powerGoodMonitor = startPowerGoodMonitor(conn);
    io.run();

    return 0;
}
