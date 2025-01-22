FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://bios-update.sh"

# software-update-dbus-interface has several flaws and isn't usable
# for production systems. Use the old updater that works.
PACKAGECONFIG:remove = "software-update-dbus-interface"
PACKAGECONFIG:append = " flash_bios"

RDEPENDS:${PN} += "bash flashrom phosphor-ipmi-ipmb bios-version"

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/bios-update.sh ${D}/${sbindir}/
}
