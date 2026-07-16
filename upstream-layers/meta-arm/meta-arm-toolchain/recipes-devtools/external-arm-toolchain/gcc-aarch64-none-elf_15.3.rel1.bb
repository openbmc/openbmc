# Copyright (C) 2020 Texas Instruments Inc.
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch64 bare-metal target (aarch64-none-elf)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"

SRC_URI = "https://gitlab.arm.com/api/v4/projects/tooling%2Fgnu-toolchains-for-arm/packages/generic/gnu-toolchain/${PV}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "a1c6fdda8b479ea3e235d38dc0994790b840648b60e9fbaf88c82ca117a7a2df"
SRC_URI[gcc-x86_64.sha256sum] = "1b07847728d455f18895f1ebd5d71a40f2ccb7cb3a84ca9a874d7f961a318ce4"

S = "${UNPACKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://gitlab.arm.com/tooling/gnu-toolchains-for-arm/-/branches"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>.+)\?"
