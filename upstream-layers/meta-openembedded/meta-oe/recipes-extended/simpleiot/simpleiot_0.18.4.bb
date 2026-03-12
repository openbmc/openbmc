# Copyright (C) 2020 Cliff Brake <cbrake@bec-systems.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Simple IOT Framework application"
HOMEPAGE = "http://simpleiot.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master;destsuffix=${GO_SRCURI_DESTSUFFIX}"

SRCREV = "1a501b7691e153969fa64e9ca58706454b160b18"

require ${BPN}-licenses.inc
require ${BPN}-go-mods.inc

LIC_FILES_CHKSUM:remove:riscv64 = "file://pkg/mod/github.com/remyoudompheng/bigfft@v0.0.0-20230129092748-24d4a6f8daec/LICENSE;md5=591778525c869cdde0ab5a1bf283cd81;spdx=BSD-3-Clause"

GO_IMPORT = "github.com/simpleiot/simpleiot"

GO_INSTALL = "${GO_IMPORT}/cmd/edge \
              ${GO_IMPORT}/cmd/fetch \
              ${GO_IMPORT}/cmd/mdns-test \
              ${GO_IMPORT}/cmd/modbus-client \
              ${GO_IMPORT}/cmd/modbus-server \
              ${GO_IMPORT}/cmd/modbus \
              ${GO_IMPORT}/cmd/point-size \
              ${GO_IMPORT}/cmd/send-sms \
              ${GO_IMPORT}/cmd/serial-encode \
              ${GO_IMPORT}/cmd/siot \
              ${GO_IMPORT}/cmd/tof10120 \
              ${GO_IMPORT}/cmd/test-resp-reader-close \
              ${GO_IMPORT}/gps \
             "

inherit go-mod go-mod-update-modules pkgconfig systemd update-rc.d

GO_LINKSHARED = ""
GO_EXTRA_LDFLAGS = "-X main.version=v${PV}"

CGO_ENABLED:x86-64 = "0"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -Dm 0644 ${S}/src/${GO_IMPORT}/contrib/siot.service ${D}${systemd_unitdir}/system/siot.service
        sed -i "s:ExecStart=/usr/bin/siot:ExecStart=/bin/sh -c \"cd /data; /usr/bin/siot\":" \
            ${D}${systemd_unitdir}/system/siot.service
    else
        install -Dm 0755 ${S}/src/${GO_IMPORT}/contrib/siot.init ${D}${sysconfdir}/init.d/siot
    fi
}

SYSTEMD_SERVICE:${PN} = "siot.service"

INITSCRIPT_NAME = "siot"
INITSCRIPT_PARAMS = "start 99 5 . stop 20 6 ."

PACKAGES =+ "${PN}-ptest ${PN}-modbus"

FILES:${PN}-ptest = "${bindir}/tof10120 \
                     ${bindir}/edge \
                     ${bindir}/mdns-test \
                     ${bindir}/test-resp-reader-close \
                     ${bindir}/send-sms \
                     ${bindir}/point-size \
                     ${bindir}/fetch \
                     ${bindir}/serial-encode \
                    "
FILES:${PN}-modbus = "${bindir}/modbus \
                      ${bindir}/modbus-client \
                      ${bindir}/modbus-server \
                     "

# .gnu_hash section serves no purpose for statically linked binaries
# and would only add unnecessary size to the executable. Disable the QA check
INSANE_SKIP:${PN} += "ldflags"
INSANE_SKIP:${PN}-ptest += "ldflags"
INSANE_SKIP:${PN}-modbus += "ldflags"

# we use sqlite and not all architectures seem to be supported see
# https://pkg.go.dev/modernc.org/sqlite#hdr-Supported_platforms_and_architectures
COMPATIBLE_HOST = "(x86_64.*|i.86.*|arm.*|aarch64.*|riscv64.*|powerpc64le.*)-(linux)"
