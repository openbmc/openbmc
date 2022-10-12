OBMC_BMC_INSTANCES ?= "0"
OBMC_CHASSIS_INSTANCES ?= "0"
OBMC_HOST_INSTANCES ?= "0"
OBMC_POWER_INSTANCES ?= "0"

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

# obmc-sensors-hwmon
#
# Provides an implementation of xyz.openbmc_project.Sensor.Value
# for hwmon sensors.
VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "phosphor-hwmon"

# phosphor-hwmon-config
#
# The phosphor-hwmon package can have its configuration generated
# automatically if the MRW feature is enabled.
VIRTUAL-RUNTIME_phosphor-hwmon-config ?= ""

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

# obmc-libobmc-intf doesn't compile with security flags enabled
SECURITY_CFLAGS:pn-obmc-libobmc-intf = ""

# We generally want to enable LTO with meson
EXTRA_OEMESON:append:class-target = " -Db_lto=true"

# Set the root password to '0penBmc'
# Override this in your local.conf
inherit extrausers

# This is the default password for the OpenBMC root user account (0penBmc)
# salted and hashed with SHA512 suitable for /etc/shadow or `usermod -p`.
DEFAULT_OPENBMC_PASSWORD = "'\$6\$UGMqyqdG\$GqTb3tXPFx9AJlzTw/8X5RoW2Z.100dT.acuk8AFJfNQYr.ZRL8itMIgLqsdq46RNHgiv78XayOSl.IbR4DFU.'"

EXTRA_USERS_PARAMS:pn-obmc-phosphor-image = " \
  usermod -p ${DEFAULT_OPENBMC_PASSWORD} root; \
  "
