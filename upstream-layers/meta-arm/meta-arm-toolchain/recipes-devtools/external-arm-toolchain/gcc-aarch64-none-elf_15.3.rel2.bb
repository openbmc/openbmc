# Copyright (C) 2020 Texas Instruments Inc.
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch64 bare-metal target (aarch64-none-elf)"
LICENSE = "GPL-3.0-only AND GPL-3.0-or-later WITH GCC-exception-3.1"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"

SRC_URI = "https://gitlab.arm.com/api/v4/projects/tooling%2Fgnu-toolchains-for-arm/packages/generic/gnu-toolchain/${PV}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-x86_64.sha256sum] = "1e4dc409cf4d33ef0a2e92c345f9e340302d355858de9bc3353758b6463895b0"
SRC_URI[gcc-aarch64.sha256sum] = "63942f3f86e69d7beefa2b946bdb23437628529e7b29f97d32229f65d0905e40"

S = "${UNPACKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://gitlab.arm.com/tooling/gnu-toolchains-for-arm/-/branches"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>.+)\?"
