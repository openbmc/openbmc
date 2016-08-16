PACKAGECONFIG_append = " networkd"
PACKAGECONFIG_remove = "machined hibernate ldconfig binfmt backlight quotacheck localed kdbus ima smack polkit"
FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"
SRC_URI += "file://0001-Export-message_append_cmdline.patch"

FILES_${PN} += "${libdir}/systemd/network/default.network"
FILES_${PN} += "${systemd_system_unitdir}/obmc-standby.target"

EXTRA_OECONF += " --disable-hwdb"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${libdir}/systemd/network/
        install -m 644 ${WORKDIR}/obmc-standby.target ${D}${systemd_system_unitdir}
        ln -sf ../obmc-standby.target \
                ${D}${systemd_system_unitdir}/multi-user.target.wants/obmc-standby.target

        #TODO Remove after this issue is resolved
        #https://github.com/openbmc/openbmc/issues/152
        ln -s /dev/null ${D}/etc/systemd/system/systemd-hwdb-update.service
}
