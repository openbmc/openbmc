# Copyright (C) 2019 Garmin Ltd. or its subsidiaries
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch32 bare-metal target (arm-none-eabi)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=0aef214b835259b64f026f4ad00c703e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=7ba3bc8ef145b48e2756a844db2029a3"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "14c0487d5753f6071d24e568881f7c7e67f80dd83165dec5164b3731394af431"
SRC_URI[gcc-x86_64.sha256sum] = "12a2815644318ebcceaf84beabb665d0924b6e79e21048452c5331a56332b309"

S = "${WORKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://developer.arm.com/tools-and-software/open-source-software/developer-tools/gnu-toolchain/downloads"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>.+)-${HOST_ARCH}-linux\.tar\.\w+"
