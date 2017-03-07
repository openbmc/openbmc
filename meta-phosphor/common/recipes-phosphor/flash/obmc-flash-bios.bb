SUMMARY = "OpenBMC org.openbmc.Flash example implementation"
DESCRIPTION = "A sample implementation for the org.openbmc.Flash DBUS API. \
org.openbmc.Flash provides APIs for functions like BIOS flash access control \
and updating."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service

RDEPENDS_${PN} += "mtd-utils-ubifs"

SRC_URI += "file://ubimount-size.conf"

SKELETON_DIR = "flashbios"
DBUS_SERVICE_${PN} += "org.openbmc.control.Flash.service"
SYSTEMD_SERVICE_${PN} += "obmc-flash-init.service"

do_install_append() {
        install -m 0755 -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/ubimount-size.conf ${D}${sysconfdir}/ubimount-size.conf
}
