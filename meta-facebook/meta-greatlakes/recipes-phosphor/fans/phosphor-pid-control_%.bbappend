FILESEXTRAPATHS:prepend:greatlakes := "${THISDIR}/${PN}:"
SRC_URI:append:greatlakes = " file://config.json \
                            "

FILES:${PN}:append:greatlakes = " ${datadir}/swampd/config.json"

do_install:append:greatlakes() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config.json ${D}${datadir}/swampd/
}
