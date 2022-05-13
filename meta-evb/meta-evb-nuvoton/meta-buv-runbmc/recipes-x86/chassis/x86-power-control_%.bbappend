FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " file://power-config-host0.json"

FILES:${PN}:append:buv-runbmc = " ${datadir}/x86-power-control/power-config-host0.json"

do_install:append:buv-runbmc() {
    install -d ${D}${datadir}/x86-power-control
    install -m 0644 -D ${WORKDIR}/power-config-host0.json \
        ${D}${datadir}/x86-power-control/power-config-host0.json
}
