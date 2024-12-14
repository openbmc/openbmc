SUMMARY = "libnvme development C library"
DESCRIPTION = "\
libnvme provides type definitions for NVMe specification structures, \
enumerations, and bit fields, helper functions to construct, dispatch, \
and decode commands and payloads, and utilities to connect, scan, and \
manage nvme devices on a Linux system."
HOMEPAGE = "https://github.com/linux-nvme/${BPN}"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & CC0-1.0 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://ccan/licenses/CC0;md5=c17af43b05840255a6fedc5eda9d56cc \
                    file://ccan/licenses/BSD-MIT;md5=838c366f69b72c5df05c96dff79b35f2"
DEPENDS = "json-c"
SRCREV = "cec9feaeb03da8046d14bb395f592c601cf2ae5f"

SRC_URI = "git://github.com/linux-nvme/libnvme;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON += "-Dkeyutils=disabled -Dopenssl=disabled -Dpython=disabled"
