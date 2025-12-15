FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://90-avahi-resolved.conf \
"

FILES:avahi-daemon:append = " \
    ${sysconfdir}/systemd/resolved.conf.d \
"

do_install:append() {
    RESOLVED_DROPIN_DIR="${D}${sysconfdir}/systemd/resolved.conf.d"
    install -d "${RESOLVED_DROPIN_DIR}"
    install -m 0644 ${UNPACKDIR}/90-avahi-resolved.conf \
        "${RESOLVED_DROPIN_DIR}/90-avahi-resolved.conf"
}
