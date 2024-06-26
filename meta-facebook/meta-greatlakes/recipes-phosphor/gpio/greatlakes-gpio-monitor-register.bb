SUMMARY = "greatlakes gpio monitor register"
DESCRIPTION = "greatlakes gpio monitor register"
SECTION = "base"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit obmc-phosphor-systemd

SRC_URI = "file://set-current-host-state@.service \
          "

DEPENDS += "systemd"

SYSTEMD_SERVICE:${PN} += "set-current-host-state@.service"
