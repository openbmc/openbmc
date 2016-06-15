OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-phosphor-event-mgmt \
        openpower-pflash \
        "

VIRTUAL-RUNTIME_obmc-phosphor-fan-ctl = " \
       skeleton \
       "
VIRTUAL-RUNTIME_obmc-phosphor-sensor-ctl = " \
       skeleton \
       "
VIRTUAL-RUNTIME_obmc-phosphor-chassis-ctl = " \
       skeleton \
       "
VIRTUAL-RUNTIME_obmc-phosphor-flash-ctl = " \
       skeleton \
       "
VIRTUAL-RUNTIME_obmc-phosphor-ipmi-parsers = " \
        host-ipmid-oem \
        host-ipmid-fru \
       "
VIRTUAL-RUNTIME_skeleton_workbook = "${MACHINE}-config"

PREFERRED_PROVIDER_virtual/obmc-phosphor-host-ipmi-hw = "btbridged"
PREFERRED_PROVIDER_virtual/obmc-phosphor-chassis-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-event-mgmt = "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-phosphor-fan-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-flash-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-policy-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-sensor-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-system-mgmt = "skeleton"
PREFERRED_PROVIDER_virtual/obmc-phosphor-user-mgmt = "obmc-phosphor-user"
