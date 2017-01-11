SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"

RDEPENDS_${PN} += "\
        libsystemd \
        "

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"

SRCREV = "8d89325adccbf7616e34210bb15ec3bebe17fcfe"

S = "${WORKDIR}/git"
