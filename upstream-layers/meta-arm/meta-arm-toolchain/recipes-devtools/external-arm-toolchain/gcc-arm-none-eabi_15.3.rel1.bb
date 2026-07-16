# Copyright (C) 2019 Garmin Ltd. or its subsidiaries
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch32 bare-metal target (arm-none-eabi)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"

SRC_URI = "https://gitlab.arm.com/api/v4/projects/tooling%2Fgnu-toolchains-for-arm/packages/generic/gnu-toolchain/${PV}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "06979e0c8171de58e5dc2a2b2019330a290f30930f27728af98a83e1a7369b3a"
SRC_URI[gcc-x86_64.sha256sum] = "563bebb2b97d53382b956d6ee1fe61e2cae26699901417234a37df505ef9b5fa"

S = "${UNPACKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://gitlab.arm.com/tooling/gnu-toolchains-for-arm/-/branches"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>.+)\?"
