SUMMARY = "FFDC collector script"
DESCRIPTION = "Command line tool to collect and tar up debug data"
DEPENDS += "systemd"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

do_install() {
       install -d ${D}${bindir}
       install -m 0755 ffdc \
                       ${D}${bindir}/ffdc
}

RDEPENDS:${PN} += " \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

require recipes-phosphor/dump/phosphor-debug-collector.inc
