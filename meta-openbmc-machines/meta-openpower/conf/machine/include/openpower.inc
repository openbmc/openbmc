OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-phosphor-event-mgmt \
        openpower-pflash \
        "

VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "btbridged"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-event-mgmt = "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-sensor-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-user-mgmt = "obmc-phosphor-user"
