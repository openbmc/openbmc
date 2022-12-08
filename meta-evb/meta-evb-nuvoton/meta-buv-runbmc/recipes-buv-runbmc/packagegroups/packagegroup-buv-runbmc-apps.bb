SUMMARY = "OpenBMC for BUV RunBMC system - Applications"
PR = "r1"

inherit packagegroup
inherit buv-entity-utils

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN}-chassis \
    ${PN}-fans \
    ${PN}-system \
    ${@entity_enabled(d, '${PN}-entity')} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'buv-dev', '${PN}-dev', '', d)} \
    "

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-system = "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "BUV RunBMC Chassis"
RDEPENDS:${PN}-chassis = " \
    x86-power-control \
    "

SUMMARY:${PN}-fans = "BUV RunBMC Fans"
RDEPENDS:${PN}-fans = " \
    phosphor-pid-control \
    "

SUMMARY:${PN}-system = "BUV RunBMC System"
RDEPENDS:${PN}-system = " \
    ipmitool \
    webui-vue \
    phosphor-host-postd \
    loadsvf \
    obmc-console \
    phosphor-sel-logger \
    rsyslog \
    obmc-ikvm \
    iperf3 \
    iperf2 \
    usb-network \
    nmon \
    memtester \
    usb-emmc-storage \
    loadmcu \
    openssl-bin \
    openssl-engines \
    nuvoton-ipmi-oem \
    "

RDEPENDS:${PN}-system:append = " \
        ${@entity_enabled(d, '', 'first-boot-set-psu')} \
        "

SUMMARY:${PN}-entity = "BUV RunBMC entity"
#RDEPENDS:${PN}-entity = " \
#    intel-ipmi-oem \
#    "

SUMMARY:${PN}-dev = "BUV RunBMC development tools"
RDEPENDS:${PN}-dev = " \
    ent \
    dhrystone \
    rw-perf \
    htop \
    gptfdisk \
    parted \
    "

