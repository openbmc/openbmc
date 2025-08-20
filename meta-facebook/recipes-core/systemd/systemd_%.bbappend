FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://journald-size-policy-10MB.conf \
    file://journald-storage-policy.conf \
    file://systemd-networkd-only-wait-for-one.conf \
"

SRC_URI:append:fb-compute = " \
    file://serial-getty@.conf \
"

do_install:append() {

    install -m 644 -D \
        ${UNPACKDIR}/journald-size-policy-10MB.conf \
        ${D}${systemd_unitdir}/journald.conf.d/journald-size-policy-10MB.conf

    install -m 644 -D \
        ${UNPACKDIR}/journald-storage-policy.conf \
        ${D}/${systemd_unitdir}/journald.conf.d/journald-storage-policy.conf

    install -m 644 -D \
        ${UNPACKDIR}/systemd-networkd-only-wait-for-one.conf \
        ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/systemd-networkd-only-wait-for-one.conf
}

do_install:append:fb-compute() {

    install -m 644 -D \
        ${UNPACKDIR}/serial-getty@.conf \
        ${D}${systemd_system_unitdir}/serial-getty@.service.d/serial-getty@.conf
}
