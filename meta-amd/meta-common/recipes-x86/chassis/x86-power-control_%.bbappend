FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"

SRC_URI:append:ethanolx = " file://power-config-host0.json"
SRC_URI:append:daytonax = " file://power-config-host0.json"

do_install:append:ethanolx() {
        install -m 0755 -d ${D}/${datadir}/${PN}
        install -m 0644 -D ${UNPACKDIR}/power-config-host0.json \
                  ${D}/${datadir}/${PN}/
}

do_install:append:daytonax() {
        install -m 0755 -d ${D}/${datadir}/${PN}
        install -m 0644 -D ${UNPACKDIR}/power-config-host0.json \
                  ${D}/${datadir}/${PN}/
}
