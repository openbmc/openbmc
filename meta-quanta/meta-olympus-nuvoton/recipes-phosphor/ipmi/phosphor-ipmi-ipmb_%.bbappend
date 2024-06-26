FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://ipmb-olympus-channels.json"
FILES:${PN}:append:olympus-nuvoton = " ${datadir}/ipmbbridge/ipmb-channels.json"

do_install:append:olympus-nuvoton() {
    install -d ${D}${datadir}/ipmbbridge
    install -m 0644 -D ${UNPACKDIR}/ipmb-olympus-channels.json \
        ${D}${datadir}/ipmbbridge/ipmb-channels.json
}

