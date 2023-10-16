FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " \
                file://ampere_power_on_failure_check.sh \
                file://ampere-host-on-host-check-override.conf \
           "

FILES:${PN} += "${systemd_system_unitdir}/ampere-host-on-host-check@0.service.d"

do_install:append() {
     install -m 0755 ${WORKDIR}/ampere_power_on_failure_check.sh ${D}/${sbindir}/

     install -d ${D}${systemd_system_unitdir}/ampere-host-on-host-check@0.service.d
     install -m 644 ${WORKDIR}/ampere-host-on-host-check-override.conf \
        ${D}${systemd_system_unitdir}/ampere-host-on-host-check@0.service.d
}
