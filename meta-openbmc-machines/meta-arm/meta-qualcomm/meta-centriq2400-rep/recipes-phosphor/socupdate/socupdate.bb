SUMMARY = "Qualcomm SOC update"
DESCRIPTION = ""
HOMEPAGE = ""
PR = "r1"

inherit obmc-phosphor-license

SRC_URI += "file://socupdate.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/socupdate.sh ${D}${bindir}/socupdate.sh
}
