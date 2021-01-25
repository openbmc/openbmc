OBMC_BMC_INSTANCES ?= "0"
OBMC_CHASSIS_INSTANCES ?= "0"
OBMC_CHECKSTOP_INSTANCES ?= "0"
OBMC_FAN_INSTANCES ?= ""
OBMC_HOST_INSTANCES ?= "0"
OBMC_POWER_BUTTON_INSTANCES ?= "0"
OBMC_POWER_INSTANCES ?= "0"
OBMC_RESET_BUTTON_INSTANCES ?= "0"
OBMC_WATCHDOG_INSTANCES ?= "0"

# obmc-bmc-state-manager
#
# Provides an implementation of the xyz.openbmc_project.State.BMC
# interface.
VIRTUAL-RUNTIME_obmc-bmc-state-manager ?= "phosphor-state-manager-bmc"

# obmc-chassis-state-manager
#
# Provides an implementation of the xyz.openbmc_project.State.Chassis
# interface.
VIRTUAL-RUNTIME_obmc-chassis-state-manager ?= "phosphor-state-manager-chassis"

# obmc-host-state-manager
#
# Provides an implementation of the xyz.openbmc_project.State.Host
# interface.
VIRTUAL-RUNTIME_obmc-host-state-manager ?= "phosphor-state-manager-host"

# obmc-discover-system-state
#
# Provides an implementation of phosphor-discover-system-state, which, if the
# host is powered off, will check the current power policy and, if necessary,
# power the host back on.
VIRTUAL-RUNTIME_obmc-discover-system-state ?= "phosphor-state-manager-discover"

# obmc-fan-presence
#
# Provides implementations of fan presence detection
VIRTUAL-RUNTIME_obmc-fan-presence ?= "phosphor-fan-presence-tach"

# obmc-fan-control
#
# Provides implementations of fan control
VIRTUAL-RUNTIME_obmc-fan-control ?= "phosphor-fan-control"

# obmc-inventory-manager
#
# Provides an implementation of the xyz.openbmc_project.Inventory.Manager
# interface.
VIRTUAL-RUNTIME_obmc-inventory-manager ?= "phosphor-inventory-manager"

# obmc-led-manager
#
# Provides an implementation of the xyz.openbmc_project.ledmanager interface.
VIRTUAL-RUNTIME_obmc-leds-manager ?= "phosphor-led-manager-ledmanager"

# obmc-led-monitor
#
# Provides an implementation of the FRU fault monitor.
VIRTUAL-RUNTIME_obmc-led-monitor ?= "phosphor-led-manager-faultmonitor"

# obmc-led-sysfs
#
# Provides an implementation of xyz.openbmc_project.Led.Physical for
# Linux kernel LED subsystem LEDs.
VIRTUAL-RUNTIME_obmc-leds-sysfs ?= "phosphor-led-sysfs"

# obmc-bmc-download-mgr
#
# Provides an implementation of the xyz.openbmc_project.Common.TFTP
# interface.
VIRTUAL-RUNTIME_obmc-bmc-download-mgr ?= "phosphor-software-manager-download-mgr"

# obmc-bmc-version
#
# Provides an implementation of the xyz.openbmc_project.Software.Version
# interface.
VIRTUAL-RUNTIME_obmc-bmc-version ?= "phosphor-software-manager-version"

# obmc-bmc-updater
#
# Provides an implementation of the xyz.openbmc_project.Software.Activation
# interface.
VIRTUAL-RUNTIME_obmc-bmc-updater ?= "phosphor-software-manager-updater"

# obmc-sensors-hwmon
#
# Provides an implementation of xyz.openbmc_project.Sensor.Value
# for hwmon sensors.
VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "phosphor-hwmon"

# obmc-dump-manager
# Provide support for managing dump
VIRTUAL-RUNTIME_obmc-dump-manager ?= "phosphor-debug-collector-manager"

# obmc-dump-monitor
# Provide support for monitoring BMC core dumps
VIRTUAL-RUNTIME_obmc-dump-monitor ?= "phosphor-debug-collector-monitor"

PREFERRED_PROVIDER_virtual/obmc-system-mgmt ??= "obmc-phosphor-sysd"

# phosphor-hwmon-config
#
# The phosphor-hwmon package can have its configuration generated
# automatically if the MRW feature is enabled.
VIRTUAL-RUNTIME_phosphor-hwmon-config ?= ""

# obmc-network-manager
#
# Provide support for managing network
VIRTUAL-RUNTIME_obmc-network-manager ?= "phosphor-network"

# Configuration overrides for phosphor-ipmi-fru.
#
# The phosphor-ipmi-fru application is data-driven and requires an input
# mapping of how the host firmware reports inventory via IPMI and the mapping
# of that inventory metadata to DBus objects interfaces and properties.

# The phosphor-ipmi-fru application is data-driven and requires an input
# mapping of how the host firmware reports inventory via IPMI.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-ipmi-fru build process.
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-hostfw-config ?= "phosphor-ipmi-fru-hostfw-config-example-native"

# The phosphor-ipmi-fru application is data-driven and requires an input
# mapping of IPMI inventory metadata to DBus objects interfaces and properties.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-ipmi-fru build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-inventory ?= \
    "phosphor-ipmi-fru-inventory-example-native"

PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-properties ?= \
    "phosphor-ipmi-fru-properties-native"

PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-read-inventory ?= \
    "phosphor-ipmi-fru-read-inventory-example-native"

# Configuration overrides for phosphor-host-ipmid.
#
# The phosphor-host-ipmid application is data-driven.

# The phosphor-host-ipmid application is data-driven and requires an input
# mapping of IPMI inventory sensor metadata to DBus objects interfaces and properties.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-host-ipmid build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-ipmi-sensor-inventory ?= \
    "phosphor-ipmi-sensor-inventory-native"

# The phosphor-host-ipmid application is data-driven and requires an input
# mapping of inventory object path to the sensor number and sensor metadata.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-host-ipmid build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-ipmi-inventory-sel ?= \
    "phosphor-ipmi-inventory-sel-native"

# Various other overrides.

# The phosphor-led-manager application is data-driven and requires an input
# yaml of LED group and members.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-led-manager build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native ?= \
    "phosphor-led-manager-config-example-native"

# The phosphor-logging application has a data driven plugin that adds FRU
# callout information to error logs, when applications generating error logs
# add the requisite callout metadata to the systemd journal.  The plugin requires
# an input mapping of Linux sysfs devices to OpenBMC DBus objects.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-logging build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-logging-callouts ?= \
    "phosphor-logging-callouts-example-native"

# The phosphor-fan-presence application is data driven and requires an input
# YAML that maps fans to their tach sensors.  This virtual is a recipe that
# provides that mapping by installing configuration files in the format and
# sysroot location expected by the phosphor-fan-presence build process in the
# sysroot.
PREFERRED_PROVIDER_virtual/phosphor-fan-presence-config ?= \
    "phosphor-fan-presence-config"

# The phosphor-fan-control application is data driven and requires a YAML file
# to define the fans in the system.  This virtual is a recipe that provides
# that definition by installing the YAML file in the format and sysroot
# location expected by the phosphor-fan-control build process in the sysroot.
PREFERRED_PROVIDER_virtual/phosphor-fan-control-fan-config ?= \
    "phosphor-fan-control-fan-config"

# The phosphor-gpio-monitor application is data-driven and requires an input.
# The application then watches the passed in input event entry for the
# change in polarity for the value matching user input. Once the required
# condition is met, it calls into a systemd target file if one is mentioned
# in input.
PREFERRED_PROVIDER_virtual/obmc-gpio-monitor ?= "phosphor-gpio-monitor"

# The phosphor-watchdog application is data-driven and requires an input.
# The application then starts a timer in disabled state. User of this timer
# needs to first enable and then use it. This also provides a way for changing
# the timeout if needed.
PREFERRED_PROVIDER_virtual/obmc-watchdog ?= "phosphor-watchdog"

# obmc-user-mgmt
#
# Provides an implementation of xyz.openbmc_project.User.Manager catering
# to user management
VIRTUAL-RUNTIME_obmc-user-mgmt ?= "phosphor-user-manager"

OBMC_DBUS_PATH_ROOT ?= "/xyz/openbmc_project"
OBMC_DBUS_IFACE_ROOT ?= "xyz.openbmc_project"

# VIRTUAL-RUNTIME_phosphor-ipmi-providers are the set of shared library
# plugins for the host-ipmid application.  By default the IPMI FRU plugin
# is enabled.  Additionally the host-ipmid recipe uses
# VIRTUAL-RUNTIME_phosphor-ipmi-providers to compute a list of potential
# IPMI whitelist recipe dependencies.  For example:
#
# VIRTUAL-RUNTIME_phosphor-ipmi-providers ?= "phosphor-ipmi-fru"
#
# Will add a build dependency to host-ipmid on the
# phosphor-ipmi-fru-whitelist-native recipe.
VIRTUAL-RUNTIME_phosphor-ipmi-providers ?= "phosphor-ipmi-fru"

# Set security headers on builds
require conf/distro/include/security_flags.inc

# Disable security on croserver.
# Remove if https://github.com/open-power/eCMD/pull/433 is ever merged
SECURITY_STRINGFORMAT_pn-croserver = ""
# obmc-libobmc-intf doesn't compile with security flags enabled
SECURITY_CFLAGS_pn-obmc-libobmc-intf = ""

# remove when https://gerrit.openbmc-project.xyz/c/openbmc/pldm/+/39984
# is merged and rev bumped
SECURITY_CFLAGS_pn-pldm = ""

# Set the root password to '0penBmc'
inherit extrausers

EXTRA_USERS_PARAMS_pn-obmc-phosphor-image = " \
  usermod -p '\$1\$UGMqyqdG\$FZiylVFmRRfl9Z0Ue8G7e/' root; \
  "
