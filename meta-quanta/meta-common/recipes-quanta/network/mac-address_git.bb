LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit systemd

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/quanta-bmc/mac-address.git;protocol=git"
SRCREV = "08b87370c56ff69df852eca87391ae46c05d437a"

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"

FILES_${PN} += "${bindir}/mac-address"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "mac-address.service"

EXTRA_OECONF = " \
    SYSTEMD_TARGET="multi-user.target" \
"
