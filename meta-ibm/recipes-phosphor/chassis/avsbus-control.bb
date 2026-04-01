SUMMARY = "AVSBus control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "i2c-tools"

S = "${UNPACKDIR}"


TMPL_EN = "avsbus-enable@.service"
TMPL_DIS = "avsbus-disable@.service"
INSTFMT_EN = "avsbus-enable@{0}.service"
INSTFMT_DIS = "avsbus-disable@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_EN = "../${TMPL_EN}:${TGTFMT}.requires/${INSTFMT_EN}"
FMT_DIS = "../${TMPL_DIS}:${TGTFMT}.requires/${INSTFMT_DIS}"

