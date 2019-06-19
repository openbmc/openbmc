SUMMARY = "Tool to assist in network address calculations for IPv4 and IPv6."
HOMEPAGE = "https://github.com/nmav/ipcalc"

SECTION = "net"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"
SRCREV = "c3ee70c878b9c5833a77a1f339f1ca4dc6f225c5"
SRC_URI = "\
    git://github.com/nmav/ipcalc.git;protocol=https; \
    file://0001-Makefile-pass-extra-linker-flags.patch \
"

export USE_GEOIP = "no"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/ipcalc ${D}/${bindir}
}
