FILESEXTRAPATHS:prepend:= "${THISDIR}/${PN}:"
SRC_URI:append:yosemite4 = " file://config.json \
                           "

FILES:${PN}:append:yosemite4 = " ${datadir}/swampd/config.json"

do_install:append:yosemite4() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config.json ${D}${datadir}/swampd/config.json
}
