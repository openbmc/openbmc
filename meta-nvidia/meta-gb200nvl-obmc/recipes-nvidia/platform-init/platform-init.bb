SUMMARY = "Platform init for GB200NVL"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS=" \
    libgpiod \
    systemd \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI += "\
    file://meson.build \
    file://platform_init.cpp \
    file://platform_init.service \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " platform_init.service "

inherit pkgconfig meson systemd

