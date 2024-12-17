SUMMARY = "OpenBMC for kudo system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-kudo-chassis \
    ${PN}-kudo-system \
    ${PN}-kudo-fw \
    ${PN}-fan-mgmt \
    "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"

RPROVIDES:${PN}-kudo-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-kudo-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-kudo-fw += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-fan-mgmt += "virtual-obmc-fan-mgmt"

SUMMARY:${PN}-kudo-chassis = "kudo chassis"
RDEPENDS:${PN}-kudo-chassis = " \
    phosphor-skeleton-control-power \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
    "

SUMMARY:${PN}-kudo-system = "kudo System"
RDEPENDS:${PN}-kudo-system = " \
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
    phosphor-state-manager \
    smbios-mdr \
    "

SUMMARY:${PN}-kudo-fw = "kudo Firmware"
RDEPENDS:${PN}-kudo-fw = " \
    kudo-fw \
    ${VIRTUAL-RUNTIME_bios-update} \
    kudo-bmc-update \
    kudo-cpld-update \
    kudo-scp-update \
    "

SUMMARY:${PN}-fan-mgmt = "kudo fan mgmt"
RDEPENDS:${PN}-fan-mgmt = " \
    pwm-init \
    phosphor-pid-control \
    "
