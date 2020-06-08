HOMEPAGE = "https://github.com/inspur-bmc/fp5280g2-psu-update-tool"

SRC_URI = "git://github.com/inspur-bmc/fp5280g2-psu-update-tool"

SRCREV = "d9cc0d32197163197c41dc1c5e4613edd7ebacf1"

SUMMARY = "Phosphor FP5280G2 PSU Update Tool"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/git/"
do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/fp5280g2-psu-update ${D}${bindir}/fp5280g2-psu-update
}

INSANE_SKIP_${PN} = "ldflags"

