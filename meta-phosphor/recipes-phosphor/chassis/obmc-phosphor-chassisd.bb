SUMMARY = "Phosphor OpenBMC Chassis Management"
DESCRIPTION = "Phosphor OpenBMC chassis management reference implementation."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-chassis-mgmt"
RPROVIDES_${PN} += "virtual-obmc-chassis-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
