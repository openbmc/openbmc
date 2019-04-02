SUMMARY = "C++ bindings for systemd event APIs"
DESCRIPTION = "C++ bindings for systemd event APIs."
HOMEPAGE = "http://github.com/openbmc/sdeventplus"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig

DEPENDS += " \
        autoconf-archive-native \
        stdplus \
        systemd \
        "

EXTRA_OECONF_append += " \
        --disable-examples \
        --disable-tests \
        "

SRC_URI += "git://github.com/openbmc/sdeventplus"
SRCREV = "1e125d974ba0c5c69cbf3eeb52b1a94a34e3e243"

S = "${WORKDIR}/git"
