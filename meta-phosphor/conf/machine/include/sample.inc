OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        "

PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "host-ipmi-hw-example"
PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "obmc-phosphor-chassisd"
PREFERRED_PROVIDER_virtual/obmc-event-mgmt = "obmc-phosphor-event"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "obmc-phosphor-fand"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "obmc-phosphor-flashd"
PREFERRED_PROVIDER_virtual/obmc-sensor-mgmt = "obmc-phosphor-sensord"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "obmc-phosphor-sysd"
PREFERRED_PROVIDER_virtual/obmc-user-mgmt = "obmc-phosphor-user"
