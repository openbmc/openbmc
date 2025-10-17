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

S = "${WORKDIR}/git"

SRCREV = "4a943529d4d84cf45f72f32e8f25f16e29b7937b"

SRC_URI += "git://github.com/openbmc/platform-init.git;branch=master;protocol=https;branch=main"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " platform_init.service "

inherit pkgconfig meson systemd

