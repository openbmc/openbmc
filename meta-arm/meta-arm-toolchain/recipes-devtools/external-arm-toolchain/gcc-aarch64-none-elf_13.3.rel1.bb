# Copyright (C) 2020 Texas Instruments Inc.
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch64 bare-metal target (aarch64-none-elf)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=402090210d41f07263e91f760d0d1ea3"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=2a62a4d37ddad55da732679acd9edf03"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "fad7d567be5c095943d42f7078ea6f9a8452062dfe151152c2ec825814d254e0"
SRC_URI[gcc-x86_64.sha256sum] = "7fedf894040580b1db747d06ac5d4263c46e591ffe7695656d1da5accb00a159"

S = "${WORKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://developer.arm.com/downloads/-/arm-gnu-toolchain-downloads"
UPSTREAM_CHECK_REGEX = "arm-gnu-toolchain-(?P<pver>\d+\.\d*\.[A-z]*\d*).*-${HOST_ARCH}-${BINNAME}\.tar\.\w+"
