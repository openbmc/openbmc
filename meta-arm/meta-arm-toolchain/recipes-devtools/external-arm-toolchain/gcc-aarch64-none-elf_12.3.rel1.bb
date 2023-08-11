# Copyright (C) 2020 Texas Instruments Inc.
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch64 bare-metal target (aarch64-none-elf)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=0aef214b835259b64f026f4ad00c703e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=7ba3bc8ef145b48e2756a844db2029a3"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "76ba68db4390b50f0a16386c6a011ec611a35a517ae40b7be008ca7920cfa59f"
SRC_URI[gcc-x86_64.sha256sum] = "382c8c786285e415bc0ff4df463e101f76d6f69a894b03f132368147c37f0ba7"

S = "${WORKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://developer.arm.com/tools-and-software/open-source-software/developer-tools/gnu-toolchain/gnu-a/downloads"
UPSTREAM_CHECK_REGEX = "gcc-arm-(?P<pver>.+)-${HOST_ARCH}-${BINNAME}\.tar\.\w+"
