SUMMARY = "transformers init service"
DESCRIPTION = "Essential init commands for transformers"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"


FILESEXTRAPATHS:prepend := "${THISDIR}/transformers-init:"
SRC_URI += "file://transformers-init.sh"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 transformers-init.sh ${D}${sbindir}
}

SYSTEMD_SERVICE:${PN} += "transformers-init.service"
