DESCRIPTION = "Monitors where the system spends its time at start, creating a graph of all processes, disk utilization, and wait time."
HOMEPAGE = "http://meego.gitorious.org/meego-developer-tools/bootchart"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067"

PV = "1.17"
PR = "r1"
PE = "1"

SRC_URI = "git://gitorious.org/meego-developer-tools/bootchart.git;protocol=https \
           file://0001-svg-add-rudimentary-support-for-ARM-cpuinfo.patch \
           file://0002-svg-open-etc-os-release-and-use-PRETTY_NAME-for-the-.patch \
"

SRCREV = "a2c7561d4060a9f075339bda89e793c76f2ff6dd"

S = "${WORKDIR}/git"

inherit autotools

