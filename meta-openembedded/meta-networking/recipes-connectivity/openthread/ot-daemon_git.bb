# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: Apache-2.0
SUMMARY = "OpenThread Daemon is an OpenThread POSIX build mode that runs OpenThread as a service."
SECTION = "net"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=543b6fe90ec5901a683320a36390c65f \
                    file://third_party/mbedtls/repo/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    "
DEPENDS = "readline"
SRCREV = "7dfde1f12923f03c9680be4d838b94b7a2320324"
PV = "0.1+git"

SRC_URI = "git://github.com/openthread/openthread.git;protocol=https;branch=main \
           file://0001-bn_mul.h-fix-x86-PIC-inline-ASM-compilation-with-GCC.patch \
           file://mbedtls.patch \
           "

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DOT_DAEMON=ON \
                 -DOT_SPINEL_RESET_CONNECTION=ON \
                 -DOT_THREAD_VERSION=1.2 \
                 -DOT_COVERAGE=OFF \
                 -DOT_PLATFORM=posix \
                 -DCMAKE_BUILD_TYPE=Release \
                 "
