SUMMARY = "CrowdSec is a free, modern & collaborative behavior detection engine, coupled with a global IP reputation network."
DESCRIPTION = "Open-source and participative security solution offering crowdsourced protection against malicious IPs and access to the most advanced real-world CTI."
HOMEPAGE = "https://www.crowdsec.net"
LICENSE = "MIT & CC0-1.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1e58fe4126ce0b50677f3aa6ba8e13c2 \
                    file://src/${GO_IMPORT}/build/windows/Chocolatey/crowdsec/tools/LICENSE.txt;md5=4d249f04094c9fb4d2b6fd2b1127e219 \
                    file://src/${GO_IMPORT}/test/lib/bats-assert/LICENSE;md5=7bae63a234e80ee7c6427dce9fdba6cc \
                    file://src/${GO_IMPORT}/test/lib/bats-core/LICENSE.md;md5=2970203aedf9e829edb96a137a4fe81b \
                    file://src/${GO_IMPORT}/test/lib/bats-file/LICENSE;md5=7bae63a234e80ee7c6427dce9fdba6cc \
                    file://src/${GO_IMPORT}/test/lib/bats-support/LICENSE;md5=7bae63a234e80ee7c6427dce9fdba6cc \
"

CROWDSEC_BRANCH_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
SRC_URI = "gitsm://${GO_IMPORT};protocol=https;branch=releases/${CROWDSEC_BRANCH_VERSION}.x;tag=v${PV};destsuffix=${GO_SRCURI_DESTSUFFIX}"

SRCREV = "027974f20cf422bbe1406cf4297fbff37417bd03"

require ${BPN}-licenses.inc
require ${BPN}-go-mods.inc
GO_IMPORT = "github.com/crowdsecurity/crowdsec"

inherit go-mod go-mod-update-modules

do_install:append() {
    # Avoid QA failure in !usrmerge case
    sed -i -e "1s,#!/usr/bin/bash,#!${base_bindir}/bash," ${D}${libdir}/go/src/${GO_IMPORT}/test/lib/bats-core/.github/workflows/check_pr_label.sh
}

RDEPENDS:${PN}-dev += "bash make"
