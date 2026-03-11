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
SRCREV = "9f88ca02886595b5aad2b60007865812fd78aa98"
PV = "0.1+git"

SRC_URI = "git://github.com/openthread/openthread.git;protocol=https;branch=main \
           "


inherit cmake

EXTRA_OECMAKE = "-DOT_DAEMON=ON \
                 -DOT_SPINEL_RESET_CONNECTION=ON \
                 -DOT_THREAD_VERSION=1.2 \
                 -DOT_COVERAGE=OFF \
                 -DOT_PLATFORM=posix \
                 -DCMAKE_BUILD_TYPE=Release \
                 "

EXTRA_OECMAKE:append:libc-musl = " -DOT_TARGET_OPENWRT=ON"

CXXFLAGS += "-DOPENTHREAD_CONFIG_LOG_PREPEND_UPTIME=0"
