FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

RDEPENDS:${PN} = "bash"

SRC_URI += " \
            file://ampere_fault_monitor.sh \
           "

do_install() {
    install -d ${D}/${sbindir}
    install -m 755 ${WORKDIR}/ampere_fault_monitor.sh ${D}/${sbindir}/
}
