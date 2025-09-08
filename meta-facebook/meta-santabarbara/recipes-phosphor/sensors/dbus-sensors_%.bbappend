FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " \
    nvmesensor \
    cablemonitor \
"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', ' file://cable-config.json', '', d)}"

do_install:append:() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', 'true', 'false', d)}; then
        install -d ${D}/var/lib/cablemonitor
        install -m 0755 ${UNPACKDIR}/cable-config.json ${D}/var/lib/cablemonitor/cable-config.json
    fi
}
