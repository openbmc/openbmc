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
        systemd \
        "

EXTRA_OECONF_append += " \
        --disable-examples \
        --disable-tests \
        "

SRC_URI += "git://github.com/openbmc/sdeventplus"
SRCREV = "7f58b2a1ec99272e0c192a1981df4ea13284bc1f"

S = "${WORKDIR}/git"
