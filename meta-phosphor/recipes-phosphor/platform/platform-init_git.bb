SUMMARY = "OpenBMC Platform init"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS = " \
    cli11 \
    i2c-tools \
    libgpiod \
    sdbusplus \
    systemd \
"

SRCREV = "def894dc92816dfa9a13c307e9d3adabbdd97ffd"

SRC_URI += "git://github.com/openbmc/platform-init.git;branch=master;protocol=https;branch=main"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " platform_init.service "

inherit pkgconfig meson systemd

