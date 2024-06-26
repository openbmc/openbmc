FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# increase the timeouts for our use-case,
# refer to this layer's README.md

EXTRA_OEMESON:append = " \
  -Dresponse-time-out=4800 \
  -Ddbus-timeout-value=10 \
"

SRC_URI:append = " file://host_eid "

do_install:append() {
    install -d ${D}/usr/share/pldm/bios
    install -D -m 0644 ${UNPACKDIR}/host_eid ${D}/usr/share/pldm
}
