# Copyright (C) 2019 Garmin Ltd. or its subsidiaries
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch32 bare-metal target (arm-none-eabi)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=be4f8b5ff7319cd54f6c52db5d6f36b0"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=1f07179249795891179bb3798bac7887"

BINNAME = "arm-none-eabi"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/gcc-arm-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "ef1d82e5894e3908cb7ed49c5485b5b95deefa32872f79c2b5f6f5447cabf55f"
SRC_URI[gcc-x86_64.sha256sum] = "8c5acd5ae567c0100245b0556941c237369f210bceb196edfe5a2e7532c60326"

S = "${WORKDIR}/gcc-arm-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://developer.arm.com/tools-and-software/open-source-software/developer-tools/gnu-toolchain/downloads"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>.+)-${HOST_ARCH}-linux\.tar\.\w+"
