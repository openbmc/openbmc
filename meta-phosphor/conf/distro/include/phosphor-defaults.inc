OBMC_CHASSIS_INSTANCES ?= "0"
OBMC_POWER_INSTANCES ?= "0"
OBMC_CHECKSTOP_INSTANCES ?= "0"
OBMC_HOST_INSTANCES ?= "0"
OBMC_WATCHDOG_INSTANCES ?= "0"
OBMC_RESET_BUTTON_INSTANCES ?= "0"
OBMC_POWER_BUTTON_INSTANCES ?= "0"
OBMC_BMC_INSTANCES ?= "0"
OBMC_FAN_INSTANCES ?= ""

PREFERRED_PROVIDER_virtual/obmc-wsgihost ?= "phosphor-gevent"
PREFERRED_PROVIDER_virtual/obmc-event-mgmt ?= "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-user-mgmt ?= "obmc-phosphor-user"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw ?= "phosphor-ipmi-hw-example"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt ?= "obmc-phosphor-chassisd"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt ?= "obmc-phosphor-fand"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt ?= "obmc-phosphor-flashd"
PREFERRED_PROVIDER_virtual/obmc-sensor-mgmt ?= "obmc-phosphor-sensord"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt ?= "obmc-phosphor-sysd"
PREFERRED_PROVIDER_virtual/obmc-settings-mgmt ?= "phosphor-settings"
PREFERRED_PROVIDER_virtual/obmc-logging-mgmt ?= "phosphor-logging"
PREFERRED_PROVIDER_virtual/obmc-host-state-mgmt ?= "phosphor-host-state-manager"
PREFERRED_PROVIDER_virtual/obmc-chassis-state-mgmt ?= "phosphor-chassis-state-manager"

# virtual/phosphor-ipmi-fru-config
#
# The phosphor-ipmi-fru application is data-driven and requires an input
# mapping of IPMI inventory metadata to DBus objects interfaces and properties.
# This virtual is a native recipe that provides that mapping by installing
# configuration files in the format and native sysroot location expected by
# the phosphor-ipmi-fru build process in the native sysroot.
PREFERRED_PROVIDER_virtual/phosphor-ipmi-fru-config ?= "phosphor-ipmi-fru-config-example-native"
