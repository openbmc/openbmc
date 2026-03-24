SUMMARY = "FSI Services"
DESCRIPTION = "Install FSI related services"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${UNPACKDIR}"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "op-proc-control"

SYSTEMD_SERVICE:${PN} += "fsi-scan.service fsi-enable.service fsi-disable.service"

