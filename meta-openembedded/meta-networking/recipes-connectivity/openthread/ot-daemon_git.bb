# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: Apache-2.0
SUMMARY = "OpenThread Daemon is an OpenThread POSIX build mode that runs OpenThread as a service."
SECTION = "net"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=543b6fe90ec5901a683320a36390c65f \
                    file://third_party/mbedtls/repo/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d \
                    "
DEPENDS = "readline"
SRCREV = "90adc86d34e21a9e8f86d093c2190030042c4a59"
PV = "0.1+git"

SRC_URI = "git://github.com/openthread/openthread.git;protocol=https;branch=main \
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

EXTRA_OECMAKE:append:libc-musl = " -DOT_TARGET_OPENWRT=ON"
