# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "libleak detects memory leak by hooking memory functions (e.g. malloc) by LD_PRELOAD"
HOMEPAGE = "https://github.com/WuBingzheng/libleak"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README.md;beginline=18;endline=21;md5=de4f705f12cdedbe452b2c3524572b03"

DEPENDS += "libbacktrace"

PE = "1"
SRCREV = "1c56b0a2ba643db976f589ec04b246e693b6c0ce"
SRC_URI = "gitsm://github.com/WuBingzheng/libleak;protocol=https;branch=master \
           file://0001-respect-environment-variables.patch \
          "

S = "${WORKDIR}/git"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	install -Dm 0755 ${B}/libleak.so ${D}${libdir}/libleak.so
}

FILES_SOLIBSDEV = ""

FILES:${PN} += "${libdir}/libleak.so"

# libunwind does not support RISCV yet
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
