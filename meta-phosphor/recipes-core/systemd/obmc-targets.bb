SUMMARY = "Phosphor Systemd targets"
DESCRIPTION = "Provides well known Systemd synchronization points for OpenBMC."
HOMEPAGE = "http://github.com/openbmc"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch obmc-phosphor-systemd

SRC_URI += "\
    file://obmc-mapper.target \
"

SYSTEMD_SERVICE:${PN} += " \
    obmc-mapper.target \
"
