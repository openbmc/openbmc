FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://greatlakes-phosphor-multi-gpio-monitor.json"

do_install:append:() {
    install -d ${D}/usr/share/phosphor-gpio-monitor
    install -m 0644 ${WORKDIR}/greatlakes-phosphor-multi-gpio-monitor.json \
                    ${D}/usr/share/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
}
