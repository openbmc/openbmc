FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


EXTRA_OEMESON:append = " \
    -Dfru-device-resizefru=true \
    -Dnew-device-detection=false \
"

do_install:append() {
    install -m 0755 ${UNPACKDIR}/blocklist.json ${D}/usr/share/entity-manager/configurations/
}
