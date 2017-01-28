OBMC_BMC_INSTANCES ?= "0"
OBMC_CHASSIS_INSTANCES ?= "0"
OBMC_CHECKSTOP_INSTANCES ?= "0"
OBMC_FAN_INSTANCES ?= ""
OBMC_HOST_INSTANCES ?= "0"
OBMC_POWER_BUTTON_INSTANCES ?= "0"
OBMC_POWER_INSTANCES ?= "0"
OBMC_RESET_BUTTON_INSTANCES ?= "0"
OBMC_WATCHDOG_INSTANCES ?= "0"

PREFERRED_PROVIDER_virtual/obmc-bmc-state-mgmt ?= "phosphor-state-manager"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt ?= "obmc-phosphor-chassisd"
PREFERRED_PROVIDER_virtual/obmc-chassis-state-mgmt ?= "phosphor-state-manager"
PREFERRED_PROVIDER_virtual/obmc-event-mgmt ?= "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt ?= "obmc-phosphor-fand"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt ?= "obmc-phosphor-flashd"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-hw-example"
PREFERRED_PROVIDER_virtual/obmc-host-state-mgmt ?= "phosphor-state-manager"
PREFERRED_PROVIDER_virtual/obmc-logging-mgmt ?= "phosphor-logging"
PREFERRED_PROVIDER_virtual/obmc-net-ipmi ?= "phosphor-net-ipmid"
PREFERRED_PROVIDER_virtual/obmc-sensor-mgmt ?= "obmc-phosphor-sensord"
PREFERRED_PROVIDER_virtual/obmc-settings-mgmt ?= "phosphor-settings"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt ?= "obmc-phosphor-sysd"
PREFERRED_PROVIDER_virtual/obmc-user-mgmt ?= "obmc-phosphor-user"
PREFERRED_PROVIDER_virtual/obmc-wsgihost ?= "phosphor-gevent"

# virtual/phosphor-ipmi-fru-hostfw-config
#
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
    "${@cf_enabled(d, 'obmc-mrw', 'phosphor-ipmi-fru-inventory-mrw-native', 'phosphor-ipmi-fru-inventory-example-native')}"

OBMC_DBUS_PATH_ROOT ?= "/xyz/openbmc_project"
OBMC_DBUS_IFACE_ROOT ?= "xyz.openbmc_project"
