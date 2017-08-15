SUMMARY = "OpenPOWER OCC controller"
DESCRIPTION = "Application to contol the OpenPOWER On-Chip-Controller"
HOMEPAGE = "https://github.com/openbmc/openpower-occ-control"
PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative

require ${PN}.inc

SRC_URI += "file://occ-active.sh"
do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/occ-active.sh \
            ${D}${bindir}/occ-active.sh
}

DBUS_SERVICE_${PN} += "org.open_power.OCC.Control.service"
SYSTEMD_SERVICE_${PN} += "op-occ-disable@.service"

DEPENDS += " \
        sdbusplus \
        sdbusplus-native \
        phosphor-logging \
        openpower-dbus-interfaces \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces-native \
        autoconf-archive-native \
        obmc-targets \
        systemd \
        "

RDEPENDS_${PN} += " \
               sdbusplus \
               phosphor-logging \
               openpower-dbus-interfaces \
               phosphor-dbus-interfaces \
               "

EXTRA_OECONF_append = "${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'i2c-occ', ' --enable-i2c-occ', '', d)}"

# Ensure host stop target requires occ disable service
OCCDISABLE_TMPL = "op-occ-disable@.service"
HOSTSTOP_TGTFMT = "obmc-host-stop@{0}.target"
OCCDISABLE_INSTFMT = "op-occ-disable@{0}.service"
HOSTSTOP_OCCDISABLE_FMT = "../${OCCDISABLE_TMPL}:${HOSTSTOP_TGTFMT}.requires/${OCCDISABLE_INSTFMT}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'HOSTSTOP_OCCDISABLE_FMT', 'OBMC_HOST_INSTANCES')}"

S = "${WORKDIR}/git"
