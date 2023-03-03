MACHINE_FEATURES += "\
    obmc-bmc-state-mgmt \
    obmc-host-state-mgmt \
    obmc-chassis-state-mgmt \
    obmc-host-ipmi \
    obmc-phosphor-chassis-mgmt \
    obmc-phosphor-fan-mgmt \
    obmc-phosphor-flash-mgmt \
    "

PREFERRED_PROVIDER_virtual/obmc-chassis-mgmt = "packagegroup-ufispace-apps"
PREFERRED_PROVIDER_virtual/obmc-fan-mgmt = "packagegroup-ufispace-apps"
PREFERRED_PROVIDER_virtual/obmc-flash-mgmt = "packagegroup-ufispace-apps"
PREFERRED_PROVIDER_virtual/obmc-system-mgmt = "packagegroup-ufispace-apps"
PREFERRED_PROVIDER_virtual/obmc-host-ipmi-hw = "phosphor-ipmi-kcs"

OVERRIDES .= ":ufispace"

IMAGE_FEATURES:remove = "obmc-ikvm"
