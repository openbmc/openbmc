# Copyright (C) 2019 Garmin Ltd. or its subsidiaries
# Released under the MIT license (see COPYING.MIT for the terms)

require arm-binary-toolchain.inc

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"

SUMMARY = "Arm GNU Toolchain - AArch32 bare-metal target (arm-none-eabi)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM:aarch64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"
LIC_FILES_CHKSUM:x86-64 = "file://share/doc/gcc/Copying.html;md5=90014a59d1783b37a10240d4d0002c6e"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}.tar.xz;name=gcc-${HOST_ARCH}"
SRC_URI[gcc-aarch64.sha256sum] = "d061559d814b205ed30c5b7c577c03317ec447ca51cd5a159d26b12a5bbeb20c"
SRC_URI[gcc-x86_64.sha256sum] = "597893282ac8c6ab1a4073977f2362990184599643b4c5ee34870a8215783a16"

S = "${UNPACKDIR}/arm-gnu-toolchain-${PV}-${HOST_ARCH}-${BINNAME}"

UPSTREAM_CHECK_URI = "https://developer.arm.com/downloads/-/arm-gnu-toolchain-downloads"
UPSTREAM_CHECK_REGEX = "arm-gnu-toolchain-(?P<pver>\d+\.\d*\.[A-z]*\d*).*-${HOST_ARCH}-${BINNAME}\.tar\.\w+"
