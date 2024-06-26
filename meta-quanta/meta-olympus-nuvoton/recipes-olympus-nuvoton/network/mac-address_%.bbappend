FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/files:"

SRC_URI:append:olympus-nuvoton = " file://config.txt"

FILES:${PN}:append:olympus-nuvoton = " ${datadir}/mac-address/config.txt"

do_install:append:olympus-nuvoton() {
    install -d ${D}${datadir}/mac-address
    install -m 0644 -D ${UNPACKDIR}/config.txt \
        ${D}${datadir}/mac-address/config.txt
}
