SUMMARY = "Power device Workarounds"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "i2c-tools"

S = "${UNPACKDIR}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"

TMPL_WA = "power-workarounds@.service"
INSTFMT_WA = "power-workarounds@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_WA = "../${TMPL_WA}:${TGTFMT}.requires/${INSTFMT_WA}"

