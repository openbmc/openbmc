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

#include <filesystem>
#include <fstream>
#include <iostream>
#include <memory>
#include <regex>

namespace ampere
{

namespace power
{

namespace fs = std::filesystem;

constexpr size_t i2cScpAddr[2] = {0x4f, 0x4e};
constexpr size_t i2cScpBus = 2;
constexpr size_t minScpPowerLimit = 90;
constexpr size_t maxScpPowerLimit = 500;

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

static std::optional<std::string> getPowerLimitDevPath(unsigned int cpuSocket)
{
    if (cpuSocket > sizeof(i2cScpAddr)/sizeof(i2cScpAddr[0]))
        return std::nullopt;

    std::vector<fs::path> paths;
    if (!findFiles(fs::path("/sys/class/hwmon"), "acpi_power_limit", paths, 1))
        return std::nullopt;

    // Get the power limit dev corresponding to CPU socket
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
            return path.string();
    }

    return std::nullopt;
}

static uint32_t getScpPowerCap(std::string devPath)
{
    std::ifstream ifs(devPath);
    size_t scpPowerCap = 0;
    ifs >> scpPowerCap;
    ifs.close();
    return scpPowerCap;
}

static void setScpPowerCap(std::string devPath, uint32_t powerCap)
{
    if ((powerCap < minScpPowerLimit ) || (powerCap > maxScpPowerLimit))
        std::cerr << "Pwr Limit need between " << minScpPowerLimit << " and " << maxScpPowerLimit << std::endl;
    std::ofstream ofs(devPath, std::ofstream::out | std::ofstream::trunc);
    ofs << std::hex << powerCap;
    ofs.close();
}

static void setBmcPowerCap(
    std::shared_ptr<sdbusplus::asio::connection>& systemBusConnection,
    uint32_t powerCap)
{
    systemBusConnection->async_method_call(
        [](const boost::system::error_code ec) {
            if (ec)
                std::cerr << "PowerCap Set: Dbus error: " << ec;
        },
        "xyz.openbmc_project.Settings",
        "/xyz/openbmc_project/control/host0/power_cap",
        "org.freedesktop.DBus.Properties", "Set",
        "xyz.openbmc_project.Control.Power.Cap", "PowerCap",
        std::variant<uint32_t>(powerCap));
}

} // namespace power
} // namespace ampere

int main(int argc, char** argv)
{
    // Get Power Limit dev path of CPU socket 0
    auto pwrLimitPath = ampere::power::getPowerLimitDevPath(0);
    if (pwrLimitPath == std::nullopt)
    {
        std::cerr << "Unable to get Power Limit dev" << std::endl;
        return -1;
    }

    // Initialize dbus connection
    boost::asio::io_service io;
    auto conn = std::make_shared<sdbusplus::asio::connection>(io);
    conn->request_name("xyz.openbmc_project.Ampere.PowerCapping");

    // Update Power Capping value from SCP to BMC settings
    uint32_t scpPowerCap = ampere::power::getScpPowerCap(pwrLimitPath.value());
    ampere::power::setBmcPowerCap(conn, scpPowerCap);

    // Handle BMC settings changed event
    sdbusplus::bus::match::match powerMatch = sdbusplus::bus::match::match(
        static_cast<sdbusplus::bus::bus&>(*conn),
        "type='signal',member='PropertiesChanged',"
        "path='/xyz/openbmc_project/control/host0/power_cap'",
        [pwrLimitPath](sdbusplus::message::message& msg) {
            std::string interfaceName;
            boost::container::flat_map<std::string, std::variant<uint32_t>>
                propertiesList;
            msg.read(interfaceName, propertiesList);
            auto find = propertiesList.find("PowerCap");
            if (find == propertiesList.end())
                return;
            uint32_t bmcPowerCap = std::get<uint32_t>(find->second);
            ampere::power::setScpPowerCap(pwrLimitPath.value(), bmcPowerCap);
        });

    io.run();
    return 0;
}
