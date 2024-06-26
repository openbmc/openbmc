FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://greatlakes-phosphor-multi-gpio-monitor.json"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/greatlakes-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
}
