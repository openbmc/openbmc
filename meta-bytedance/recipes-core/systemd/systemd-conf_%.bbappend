FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://coredump.conf \
"

do_install:append() {
    install -m 0644 \
        ${UNPACKDIR}/coredump.conf \
        -D -t ${D}${sysconfdir}/systemd
}

FILES:${PN}:append = " \
    ${sysconfdir}/systemd/coredump.conf \
"
