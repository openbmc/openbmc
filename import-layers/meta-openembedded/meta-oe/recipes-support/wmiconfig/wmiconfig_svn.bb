SUMMARY = "Atheros 6K Wifi configuration utility"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://wmiconfig.c;endline=19;md5=4394a56bca1c5b2446c9f8e406c82911"
SECTION = "console/network"
SRCREV = "5394"
PV = "0.0.0+svnr${SRCPV}"
PR = "r2"

SRC_URI = "svn://svn.openmoko.org/trunk/src/target;module=AR6kSDK.build_sw.18;protocol=http"
S = "${WORKDIR}/AR6kSDK.build_sw.18/host/tools/wmiconfig"

CLEANBROKEN = "1"

EXTRA_OEMAKE = "-e MAKEFLAGS="

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 wmiconfig ${D}${bindir}
}

