SUMMARY = "FFDC collector script"
DESCRIPTION = "Command line tool to collect and tar up debug data"
PR = "r1"
PV = "1.0+git${SRCPV}"

require recipes-phosphor/dump/phosphor-debug-collector.inc

DEPENDS += "systemd"

RDEPENDS_${PN} += " \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

S = "${WORKDIR}/git"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}
