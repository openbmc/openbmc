FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://power-config-host0.json"

do_install:append () {
    install -m 0755 -d ${D}/${datadir}/${BPN}
    install -m 0644 -D ${WORKDIR}/*.json \
                   ${D}/${datadir}/${BPN}/
}
