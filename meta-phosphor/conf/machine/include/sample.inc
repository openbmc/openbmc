OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_obmc-phosphor-fan-ctl = " \
       obmc-phosphor-example-sdbus \
       "
VIRTUAL-RUNTIME_obmc-phosphor-sensor-ctl = " \
       obmc-phosphor-example-sdbus \
       "
VIRTUAL-RUNTIME_obmc-phosphor-chassis-ctl = " \
       obmc-phosphor-example-pydbus \
       "
VIRTUAL-RUNTIME_obmc-phosphor-flash-ctl = " \
       obmc-phosphor-example-pydbus \
       "
VIRTUAL-RUNTIME_obmc-phosphor-ipmi-parsers = " \
        host-ipmid-oem \
        host-ipmid-fru \
        host-ipmid-tool \
       "
PREFERRED_PROVIDER_virtual/obmc-phosphor-host-ipmi-hw = "host-ipmi-hw-example"
PREFERRED_PROVIDER_virtual/obmc-phosphor-chassis-mgmt = "obmc-phosphor-chassisd"
PREFERRED_PROVIDER_virtual/obmc-phosphor-event-mgmt = "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-phosphor-fan-mgmt = "obmc-phosphor-fand"
PREFERRED_PROVIDER_virtual/obmc-phosphor-flash-mgmt = "obmc-phosphor-flashd"
PREFERRED_PROVIDER_virtual/obmc-phosphor-sensor-mgmt = "obmc-phosphor-sensord"
PREFERRED_PROVIDER_virtual/obmc-phosphor-system-mgmt = "obmc-phosphor-sysd"
PREFERRED_PROVIDER_virtual/obmc-phosphor-user-mgmt = "obmc-phosphor-user"
