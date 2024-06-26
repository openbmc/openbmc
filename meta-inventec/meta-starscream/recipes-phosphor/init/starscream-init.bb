SUMMARY = "starscream init service"
DESCRIPTION = "Essential init commands for starscream"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"


FILESEXTRAPATHS:prepend := "${THISDIR}/starscream-init:"
SRC_URI += "file://starscream-init.sh \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 starscream-init.sh ${D}${sbindir}
}

SYSTEMD_SERVICE:${PN} += "starscream-init.service"
