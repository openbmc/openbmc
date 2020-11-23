FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
    file://coredump.conf \
"

do_install_append() {
    install -m 0644 \
        ${WORKDIR}/coredump.conf \
        -D -t ${D}${sysconfdir}/systemd
}

FILES_${PN}_append = " \
    ${sysconfdir}/systemd/coredump.conf \
"
