# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A C library that may be linked into a C/C++ program to produce symbolic backtraces"
HOMEPAGE = "https://github.com/ianlancetaylor/libbacktrace"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24b5b3feec63c4be0975e1fea5100440"

DEPENDS += "libunwind"

SRC_URI = "git://github.com/ianlancetaylor/libbacktrace;protocol=https;branch=master"

PV = "1.0+git${SRCPV}"
SRCREV = "4f57c999716847e45505b3df170150876b545088"

S = "${WORKDIR}/git"

inherit autotools

EXTR_OECONF += "--with-system-libunwind"

CFLAGS += "-fPIC"

do_configure() {
    oe_runconf
}

# libunwind does not support RISCV yet
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
