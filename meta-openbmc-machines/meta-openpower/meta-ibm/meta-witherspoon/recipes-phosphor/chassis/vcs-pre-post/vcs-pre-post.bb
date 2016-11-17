SUMMARY = "Witherspoon P9 power on"
DESCRIPTION = "Witherspoon power on workaround"
PR = "r1"

inherit obmc-phosphor-license

RDEPEND += "pdbg"

S = "${WORKDIR}"
SRC_URI += "file://vcs_off.sh \
            file://vcs_off@.service \
            file://vcs_on.sh \
            file://vcs_on@.service"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vcs_off.sh ${D}${bindir}/vcs_off.sh
        install -m 0755 ${WORKDIR}/vcs_on.sh ${D}${bindir}/vcs_on.sh
        install -m 0755 ${WORKDIR}/vcs_off@.service \
                        ${D}${bindir}/vcs_off@.service
        install -m 0755 ${WORKDIR}/vcs_on@.service \
                        ${D}${bindir}/vcs_on.service
}
