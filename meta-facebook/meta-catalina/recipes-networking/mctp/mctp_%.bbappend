FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://setup-local-eid.conf \
    file://setup-local-eid \
"

RDEPENDS:${PN} += " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d

    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -m 0644 ${UNPACKDIR}/setup-local-eid.conf ${override_dir}/setup-local-eid.conf

    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/setup-local-eid ${D}${libexecdir}/mctp/
}

# In poky/meta/recipes-core/systemd/systemd_257.5.bb, Predictable
# Network Interface Names has been disabled. We need to enable
# it for mctp-usb devices by adding another link file.
SRC_URI:append:clemente = "file://80-mctp-usb.link "

FILES:${PN}:append:clemente = " ${systemd_unitdir}"

do_install:append:clemente() {
    install -d 0755 ${D}${systemd_unitdir}/network
    install -m 0644 ${UNPACKDIR}/80-mctp-usb.link \
      ${D}${systemd_unitdir}/network/
}

