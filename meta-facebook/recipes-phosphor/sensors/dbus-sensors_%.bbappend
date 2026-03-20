FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS = " \
    exitairtempsensor \
    external \
    intelcpusensor \
    intrusionsensor \
    ipmbsensor \
    mcutempsensor \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fb-fanless', 'fansensor', '',d)} \
"
PACKAGECONFIG:remove = " \
    ${FACEBOOK_REMOVED_DBUS_SENSORS} \
"

PACKAGECONFIG:append:mf-fb-liquid-cooled = " \
    leakdetector \
"

PACKAGECONFIG:append:fb-compute-nvidia = " \
    smbpbi \
"

RDEPENDS:${PN}:append:mf-fb-liquid-cooled = " bash"

LEAK_SERVICES:mf-fb-liquid-cooled = " \
    file://xyz.openbmc_project.leakdetector.critical.assert@.service \
    file://xyz.openbmc_project.leakdetector.critical.deassert@.service \
    file://xyz.openbmc_project.leakdetector.warning.assert@.service \
    file://xyz.openbmc_project.leakdetector.warning.deassert@.service \
"
LEAK_HANDLERS:mf-fb-liquid-cooled = " \
    file://critical-leak-assert-handler \
    file://critical-leak-assert-handler-platform.sh \
    file://critical-leak-deassert-handler \
    file://warning-leak-assert-handler \
    file://warning-leak-deassert-handler \
"
SRC_URI:append:mf-fb-liquid-cooled = " ${LEAK_SERVICES} ${LEAK_HANDLERS}"

do_install:append:mf-fb-liquid-cooled() {
    install -d ${D}${systemd_system_unitdir}
    for svc in ${LEAK_SERVICES}; do
        svc=$(basename ${svc#file://})
        install -m 0644 ${UNPACKDIR}/${svc} ${D}${systemd_system_unitdir}/${svc}
    done

    install -d ${D}${libexecdir}/${PN}
    for handler in ${LEAK_HANDLERS}; do
        handler=$(basename ${handler#file://})
        install -m 0755 ${UNPACKDIR}/${handler} ${D}${libexecdir}/${PN}/${handler}
    done
}

FILES:${PN}:append:mf-fb-liquid-cooled = " ${systemd_system_unitdir}/xyz.openbmc_project.leakdetector*@.service"
