SUMMARY = "OpenBMC for KUDO system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
     ${PN}-kudo-system \
     ${PN}-kudo-common-utils \
     "

PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES_${PN}-kudo-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-kudo-system = "KUDO System"
RDEPENDS_${PN}-kudo-system = " \
    google-ipmi-sys \
    google-ipmi-i2c \
    "

SUMMARY_${PN}-kudo-common-utils = "KUDO common utils"
RDEPENDS_${PN}-kudo-common-utils = " \
    ipmitool \
    phosphor-webui \
    phosphor-host-postd \
    loadsvf \
    obmc-console \
    phosphor-sel-logger \
    "
