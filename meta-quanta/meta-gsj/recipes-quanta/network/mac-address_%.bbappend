FILESEXTRAPATHS:prepend:gsj := "${THISDIR}/${PN}:"
SRC_URI:append:gsj = " file://config.txt"

FILES:${PN}:append:gsj = " ${datadir}/mac-address/config.txt"

do_install:append:gsj() {
    install -d ${D}${datadir}/mac-address
    install -m 0644 -D ${WORKDIR}/config.txt \
        ${D}${datadir}/mac-address/config.txt
}
