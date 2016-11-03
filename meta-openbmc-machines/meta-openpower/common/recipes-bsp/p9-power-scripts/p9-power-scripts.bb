SUMMARY     = "Witherspoon P9 power on"
DESCRIPTION = "Power on code workaround for Witherspoon"
PR = "r1"

inherit obmc-phosphor-license

S = "${WORKDIR}"
SRC_URI += "file://vcs.sh"

RDEPEND += "pdbg"

do_install() {
        install -m 0755 ${WORKDIR}/vcs.sh ${D}/vcs.sh
}

FILES_${PN} += " /vcs "
