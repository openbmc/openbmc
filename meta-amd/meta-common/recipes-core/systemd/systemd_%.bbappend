FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://journald-size-policy.conf \
    file://journald-storage-policy.conf \
"

do_install:append() {

    install -m 644 -D \
        ${UNPACKDIR}/journald-size-policy.conf \
        ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy.conf

    install -m 644 -D \
        ${UNPACKDIR}/journald-storage-policy.conf \
        ${D}/${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf
}
