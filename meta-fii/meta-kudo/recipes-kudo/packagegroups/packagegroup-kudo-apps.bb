SUMMARY = "OpenBMC for kudo system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-kudo-system \
    ${PN}-kudo-fw \
    ${PN}-fan-mgmt \
    "

PROVIDES += "virtual/obmc-system-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"

RPROVIDES_${PN}-kudo-system += "virtual-obmc-system-mgmt"
RPROVIDES_${PN}-kudo-fw += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-fan-mgmt += "virtual-obmc-fan-mgmt"

SUMMARY_${PN}-kudo-system = "kudo System"
RDEPENDS_${PN}-kudo-system = " \
    ipmitool \
    ethtool \
    memtester \
    loadsvf \
    fii-ipmi-oem \
    kudo-entity-association-map \
    hotswap-power-cycle \
    obmc-console \
    usb-network \
    ncsid \
    gbmc-mac-config \
    kudo-boot \
    kudo-cmd \
    "

SUMMARY_${PN}-kudo-fw = "kudo Firmware"
RDEPENDS_${PN}-kudo-fw = " \
    kudo-fw \
    kudo-bios-update \
    kudo-bmc-update \
    kudo-cpld-update \
    kudo-scp-update \
    "

SUMMARY_${PN}-fan-mgmt = "kudo fan mgmt"
RDEPENDS_${PN}-fan-mgmt = " \
    pwm-init \
    phosphor-pid-control \
    "
