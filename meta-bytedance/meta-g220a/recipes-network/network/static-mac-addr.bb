SUMMARY = "Enforce static MAC addresses"
DESCRIPTION = "Set a priority on MAC addresses to run with: \
               factory-specified > u-boot-specified > random"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
SRC_URI = "\
    file://mac-check \
    file://${BPN}.service \
    "

inherit obmc-phosphor-systemd

SYSTEMD_SERVICE:${PN} += "${BPN}.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/mac-check ${D}${bindir}
}

RDEPENDS:${PN}:append = " bash"
