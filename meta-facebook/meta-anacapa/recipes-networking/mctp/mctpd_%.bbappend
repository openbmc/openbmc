FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://setup-local-eid.conf \
    file://mctp-config \
    file://mctpusb-hotplug \
    file://mctpusb-hotplug@.service \
    file://mctpusb-init \
    file://mctpusb-init.service \
"
RDEPENDS:${PN} += "bash"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/* \
    ${libexecdir}/mctp \
"

RDEPENDS:${PN} += " mctp"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    mctpusb-hotplug@.service \
    mctpusb-init.service \
"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d

    install -m 0644 ${UNPACKDIR}/setup-local-eid.conf \
            ${override_dir}/setup-local-eid.conf

    install -d ${D}${libexecdir}/mctp

    install -m 0755 ${UNPACKDIR}/mctp-config \
            ${D}${libexecdir}/mctp/

    install -m 0755 ${UNPACKDIR}/mctpusb-hotplug \
            ${D}${libexecdir}/mctp/

    install -m 0755 ${UNPACKDIR}/mctpusb-init \
            ${D}${libexecdir}/mctp/

    install -d ${D}${systemd_system_unitdir}

    install -m 0644 ${UNPACKDIR}/mctpusb-hotplug@.service \
            ${D}${systemd_system_unitdir}

    install -m 0644 ${UNPACKDIR}/mctpusb-init.service \
            ${D}${systemd_system_unitdir}
}
