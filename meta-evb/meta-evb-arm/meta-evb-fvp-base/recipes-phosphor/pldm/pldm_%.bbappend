FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

EXTRA_OEMESON:append = " \
                        -Doem-ibm=disabled \
                       "

SRC_URI:append = " file://host_eid "

do_install:append() {
    install -d ${D}/usr/share/pldm/bios
    install -D -m 0644 ${WORKDIR}/host_eid ${D}/usr/share/pldm
}
