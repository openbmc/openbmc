#include <ipmi-whitelist.hpp>
#include <ipmid/api.hpp>
#include <ipmid/utils.hpp>
#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/log.hpp>
#include <xyz/openbmc_project/Control/Security/RestrictionMode/server.hpp>

#include <algorithm>
#include <array>

using namespace phosphor::logging;
using namespace sdbusplus::xyz::openbmc_project::Common::Error;
using namespace sdbusplus::xyz::openbmc_project::Control::Security::server;

namespace ipmi
{

// put the filter provider in an unnamed namespace
namespace
{

/** @class WhitelistFilter
 *
 * Class that implements an IPMI message filter based
 * on incoming interface and a restriction mode setting
 */
class WhitelistFilter
{

  public:
    WhitelistFilter();
    ~WhitelistFilter() = default;
    WhitelistFilter(WhitelistFilter const&) = delete;
    WhitelistFilter(WhitelistFilter&&) = delete;
    WhitelistFilter& operator=(WhitelistFilter const&) = delete;
    WhitelistFilter& operator=(WhitelistFilter&&) = delete;

  private:
    void postInit();
    void cacheRestrictedAndPostCompleteMode();
    void handleRestrictedModeChange(sdbusplus::message::message& m);
    void handlePostCompleteChange(sdbusplus::message::message& m);
    void updatePostComplete(const std::string& value);
    void updateRestrictionMode(const std::string& value);
    ipmi::Cc filterMessage(ipmi::message::Request::ptr request);
    void handleCoreBiosDoneChange(sdbusplus::message::message& m);
    void cacheCoreBiosDone();

    // the BMC KCS Policy Control Modes document uses different names
    // than the RestrictionModes D-Bus interface; use aliases
    static constexpr RestrictionMode::Modes restrictionModeAllowAll =
        RestrictionMode::Modes::Provisioning;
    static constexpr RestrictionMode::Modes restrictionModeRestricted =
        RestrictionMode::Modes::ProvisionedHostWhitelist;
    static constexpr RestrictionMode::Modes restrictionModeDenyAll =
        RestrictionMode::Modes::ProvisionedHostDisabled;

    RestrictionMode::Modes restrictionMode = restrictionModeRestricted;
    bool postCompleted = true;
    bool coreBIOSDone = true;
    int channelSMM = -1;
    std::shared_ptr<sdbusplus::asio::connection> bus;
    std::unique_ptr<sdbusplus::bus::match::match> modeChangeMatch;
    std::unique_ptr<sdbusplus::bus::match::match> modeIntfAddedMatch;
    std::unique_ptr<sdbusplus::bus::match::match> postCompleteMatch;
    std::unique_ptr<sdbusplus::bus::match::match> postCompleteIntfAddedMatch;
    std::unique_ptr<sdbusplus::bus::match::match> platStateChangeMatch;
    std::unique_ptr<sdbusplus::bus::match::match> platStateIntfAddedMatch;

    static constexpr const char restrictionModeIntf[] =
        "xyz.openbmc_project.Control.Security.RestrictionMode";
    static constexpr const char* systemOsStatusIntf =
        "xyz.openbmc_project.State.OperatingSystem.Status";
    static constexpr const char* hostMiscIntf =
        "xyz.openbmc_project.State.Host.Misc";
};

static inline uint8_t getSMMChannel()
{
    ipmi::ChannelInfo chInfo;

    for (int channel = 0; channel < ipmi::maxIpmiChannels; channel++)
    {
        if (ipmi::getChannelInfo(channel, chInfo) != ipmi::ccSuccess)
        {
            continue;
        }

        if (static_cast<ipmi::EChannelMediumType>(chInfo.mediumType) ==
                ipmi::EChannelMediumType::systemInterface &&
            channel != ipmi::channelSystemIface)
        {
            log<level::INFO>("SMM channel number",
                             entry("CHANNEL=%d", channel));
            return channel;
        }
    }
    log<level::ERR>("Unable to find SMM Channel Info");
    return -1;
}

WhitelistFilter::WhitelistFilter()
{
    bus = getSdBus();

    log<level::INFO>("Loading whitelist filter");

    ipmi::registerFilter(ipmi::prioOpenBmcBase,
                         [this](ipmi::message::Request::ptr request) {
                             return filterMessage(request);
                         });

    channelSMM = getSMMChannel();
    // wait until io->run is going to fetch RestrictionMode
    post_work([this]() { postInit(); });
}

void WhitelistFilter::cacheRestrictedAndPostCompleteMode()
{
    std::string restrictionModePath;
    std::string restrictionModeService;
    std::string systemOsStatusPath;
    std::string systemOsStatusService;
    try
    {
        ipmi::DbusObjectInfo restrictionObj =
            ipmi::getDbusObject(*bus, restrictionModeIntf);

        restrictionModePath = restrictionObj.first;
        restrictionModeService = restrictionObj.second;

        ipmi::DbusObjectInfo postCompleteObj =
            ipmi::getDbusObject(*bus, systemOsStatusIntf);

        systemOsStatusPath = postCompleteObj.first;
        systemOsStatusService = postCompleteObj.second;
    }
    catch (const std::exception&)
    {
        log<level::ERR>(
            "Could not initialize provisioning mode, defaulting to restricted",
            entry("VALUE=%d", static_cast<int>(restrictionMode)));
        return;
    }

    bus->async_method_call(
        [this](boost::system::error_code ec, ipmi::Value v) {
            if (ec)
            {
                log<level::ERR>(
                    "Could not initialize provisioning mode, "
                    "defaulting to restricted",
                    entry("VALUE=%d", static_cast<int>(restrictionMode)));
                return;
            }
            auto mode = std::get<std::string>(v);
            restrictionMode = RestrictionMode::convertModesFromString(mode);
            log<level::INFO>(
                "Read restriction mode",
                entry("VALUE=%d", static_cast<int>(restrictionMode)));
        },
        restrictionModeService, restrictionModePath,
        "org.freedesktop.DBus.Properties", "Get", restrictionModeIntf,
        "RestrictionMode");

    bus->async_method_call(
        [this](boost::system::error_code ec, const ipmi::Value& v) {
            if (ec)
            {
                log<level::ERR>("Error in OperatingSystemState Get");
                postCompleted = true;
                return;
            }
            auto value = std::get<std::string>(v);
            if (value == "Standby")
            {
                postCompleted = true;
            }
            else
            {
                postCompleted = false;
            }
            log<level::INFO>("Read POST complete value",
                             entry("VALUE=%d", postCompleted));
        },
        systemOsStatusService, systemOsStatusPath,
        "org.freedesktop.DBus.Properties", "Get", systemOsStatusIntf,
        "OperatingSystemState");
}

void WhitelistFilter::updateRestrictionMode(const std::string& value)
{
    restrictionMode = RestrictionMode::convertModesFromString(value);
    log<level::INFO>("Updated restriction mode",
                     entry("VALUE=%d", static_cast<int>(restrictionMode)));
}

void WhitelistFilter::handleRestrictedModeChange(sdbusplus::message::message& m)
{
    std::string signal = m.get_member();
    if (signal == "PropertiesChanged")
    {
        std::string intf;
        std::vector<std::pair<std::string, ipmi::Value>> propertyList;
        m.read(intf, propertyList);
        for (const auto& property : propertyList)
        {
            if (property.first == "RestrictionMode")
            {
                updateRestrictionMode(std::get<std::string>(property.second));
            }
        }
    }
    else if (signal == "InterfacesAdded")
    {
        sdbusplus::message::object_path path;
        DbusInterfaceMap restModeObj;
        m.read(path, restModeObj);
        auto intfItr = restModeObj.find(restrictionModeIntf);
        if (intfItr == restModeObj.end())
        {
            return;
        }
        PropertyMap& propertyList = intfItr->second;
        auto itr = propertyList.find("RestrictionMode");
        if (itr == propertyList.end())
        {
            return;
        }
        updateRestrictionMode(std::get<std::string>(itr->second));
    }
}

void WhitelistFilter::updatePostComplete(const std::string& value)
{
    if (value == "Standby")
    {
        postCompleted = true;
    }
    else
    {
        postCompleted = false;
    }
    log<level::INFO>(postCompleted ? "Updated to POST Complete"
                                   : "Updated to !POST Complete");
}

void WhitelistFilter::handlePostCompleteChange(sdbusplus::message::message& m)
{
    std::string signal = m.get_member();
    if (signal == "PropertiesChanged")
    {
        std::string intf;
        std::vector<std::pair<std::string, ipmi::Value>> propertyList;
        m.read(intf, propertyList);
        for (const auto& property : propertyList)
        {
            if (property.first == "OperatingSystemState")
            {
                updatePostComplete(std::get<std::string>(property.second));
            }
        }
    }
    else if (signal == "InterfacesAdded")
    {
        sdbusplus::message::object_path path;
        DbusInterfaceMap postCompleteObj;
        m.read(path, postCompleteObj);
        auto intfItr = postCompleteObj.find(systemOsStatusIntf);
        if (intfItr == postCompleteObj.end())
        {
            return;
        }
        PropertyMap& propertyList = intfItr->second;
        auto itr = propertyList.find("OperatingSystemState");
        if (itr == propertyList.end())
        {
            return;
        }
        updatePostComplete(std::get<std::string>(itr->second));
    }
}

void WhitelistFilter::cacheCoreBiosDone()
{
    std::string coreBiosDonePath;
    std::string coreBiosDoneService;
    try
    {
        ipmi::DbusObjectInfo coreBiosDoneObj =
            ipmi::getDbusObject(*bus, hostMiscIntf);

        coreBiosDonePath = coreBiosDoneObj.first;
        coreBiosDoneService = coreBiosDoneObj.second;
    }
    catch (const std::exception&)
    {
        log<level::ERR>("Could not initialize CoreBiosDone, "
                        "coreBIOSDone asserted as default");
        return;
    }

    bus->async_method_call(
        [this](boost::system::error_code ec, const ipmi::Value& v) {
            if (ec)
            {
                log<level::ERR>(
                    "async call failed, coreBIOSDone asserted as default");
                return;
            }
            coreBIOSDone = std::get<bool>(v);
            log<level::INFO>("Read CoreBiosDone",
                             entry("VALUE=%d", static_cast<int>(coreBIOSDone)));
        },
        coreBiosDoneService, coreBiosDonePath,
        "org.freedesktop.DBus.Properties", "Get", hostMiscIntf, "CoreBiosDone");
}

void WhitelistFilter::handleCoreBiosDoneChange(sdbusplus::message::message& msg)
{
    std::string signal = msg.get_member();
    if (signal == "PropertiesChanged")
    {
        std::string intf;
        std::vector<std::pair<std::string, ipmi::Value>> propertyList;
        msg.read(intf, propertyList);
        auto it =
            std::find_if(propertyList.begin(), propertyList.end(),
                         [](const std::pair<std::string, ipmi::Value>& prop) {
                             return prop.first == "CoreBiosDone";
                         });

        if (it != propertyList.end())
        {
            coreBIOSDone = std::get<bool>(it->second);
            log<level::INFO>(coreBIOSDone ? "coreBIOSDone asserted"
                                          : "coreBIOSDone not asserted");
        }
    }
    else if (signal == "InterfacesAdded")
    {
        sdbusplus::message::object_path path;
        DbusInterfaceMap eSpiresetObj;
        msg.read(path, eSpiresetObj);
        auto intfItr = eSpiresetObj.find(hostMiscIntf);
        if (intfItr == eSpiresetObj.end())
        {
            return;
        }
        PropertyMap& propertyList = intfItr->second;
        auto itr = propertyList.find("CoreBiosDone");
        if (itr == propertyList.end())
        {
            return;
        }
        coreBIOSDone = std::get<bool>(itr->second);
        log<level::INFO>(coreBIOSDone ? "coreBIOSDone asserted"
                                      : "coreBIOSDone not asserted");
    }
}

void WhitelistFilter::postInit()
{
    // Wait for changes on Restricted mode
    namespace rules = sdbusplus::bus::match::rules;
    const std::string filterStrModeChange =
        rules::type::signal() + rules::member("PropertiesChanged") +
        rules::interface("org.freedesktop.DBus.Properties") +
        rules::argN(0, restrictionModeIntf);

    const std::string filterStrModeIntfAdd =
        rules::interfacesAdded() +
        rules::argNpath(
            0, "/xyz/openbmc_project/control/security/restriction_mode");

    const std::string filterStrPostComplete =
        rules::type::signal() + rules::member("PropertiesChanged") +
        rules::interface("org.freedesktop.DBus.Properties") +
        rules::argN(0, systemOsStatusIntf);

    const std::string filterStrPostIntfAdd =
        rules::interfacesAdded() +
        rules::argNpath(0, "/xyz/openbmc_project/state/os");

    const std::string filterStrPlatStateChange =
        rules::type::signal() + rules::member("PropertiesChanged") +
        rules::interface("org.freedesktop.DBus.Properties") +
        rules::argN(0, hostMiscIntf);

    const std::string filterStrPlatStateIntfAdd =
        rules::interfacesAdded() +
        rules::argNpath(0, "/xyz/openbmc_project/misc/platform_state");

    modeChangeMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrModeChange, [this](sdbusplus::message::message& m) {
            handleRestrictedModeChange(m);
        });
    modeIntfAddedMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrModeIntfAdd, [this](sdbusplus::message::message& m) {
            handleRestrictedModeChange(m);
        });

    postCompleteMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrPostComplete, [this](sdbusplus::message::message& m) {
            handlePostCompleteChange(m);
        });

    postCompleteIntfAddedMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrPostIntfAdd, [this](sdbusplus::message::message& m) {
            handlePostCompleteChange(m);
        });

    platStateChangeMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrPlatStateChange, [this](sdbusplus::message::message& m) {
            handleCoreBiosDoneChange(m);
        });

    platStateIntfAddedMatch = std::make_unique<sdbusplus::bus::match::match>(
        *bus, filterStrPlatStateIntfAdd,
        [this](sdbusplus::message::message& m) {
            handleCoreBiosDoneChange(m);
        });

    // Initialize restricted mode
    cacheRestrictedAndPostCompleteMode();
    // Initialize CoreBiosDone
    cacheCoreBiosDone();
}

ipmi::Cc WhitelistFilter::filterMessage(ipmi::message::Request::ptr request)
{
    auto channelMask = static_cast<unsigned short>(1 << request->ctx->channel);
    bool whitelisted = std::binary_search(
        whitelist.cbegin(), whitelist.cend(),
        std::make_tuple(request->ctx->netFn, request->ctx->cmd, channelMask),
        [](const netfncmd_tuple& first, const netfncmd_tuple& value) {
            return (std::get<2>(first) & std::get<2>(value))
                       ? first < std::make_tuple(std::get<0>(value),
                                                 std::get<1>(value),
                                                 std::get<2>(first))
                       : first < value;
        });

    // no special handling for non-system-interface channels
    if (!(request->ctx->channel == ipmi::channelSystemIface ||
          request->ctx->channel == channelSMM))
    {
        if (!whitelisted)
        {
            log<level::INFO>("Channel/NetFn/Cmd not whitelisted",
                             entry("CHANNEL=0x%X", request->ctx->channel),
                             entry("NETFN=0x%X", int(request->ctx->netFn)),
                             entry("CMD=0x%X", int(request->ctx->cmd)));
            return ipmi::ccInsufficientPrivilege;
        }
        return ipmi::ccSuccess;
    }

    // for system interface, filtering is done as follows:
    // Allow All:  preboot ? ccSuccess : ccSuccess
    // Restricted: preboot ? ccSuccess :
    //                  ( whitelist ? ccSuccess : ccInsufficientPrivilege )
    // Deny All:   preboot ? ccSuccess : ccInsufficientPrivilege

    if (!(postCompleted || coreBIOSDone))
    {
        // Allow all commands, till POST or CoreBiosDone is completed
        return ipmi::ccSuccess;
    }

    switch (restrictionMode)
    {
        case RestrictionMode::Modes::None:
        case restrictionModeAllowAll:
        {
            // Allow All
            return ipmi::ccSuccess;
            break;
        }
        case restrictionModeRestricted:
        {
            // Restricted - follow whitelist
            break;
        }
        case restrictionModeDenyAll:
        {
            // Deny All
            whitelisted = false;
            break;
        }
        default: // for whitelist and blacklist
            return ipmi::ccInsufficientPrivilege;
    }

    if (!whitelisted)
    {
        log<level::INFO>("Channel/NetFn/Cmd not whitelisted",
                         entry("CHANNEL=0x%X", request->ctx->channel),
                         entry("NETFN=0x%X", int(request->ctx->netFn)),
                         entry("CMD=0x%X", int(request->ctx->cmd)));
        return ipmi::ccInsufficientPrivilege;
    }
    return ipmi::ccSuccess;
} // namespace

// instantiate the WhitelistFilter when this shared object is loaded
WhitelistFilter whitelistFilter;

} // namespace

} // namespace ipmi
