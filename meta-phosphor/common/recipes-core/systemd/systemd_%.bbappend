PACKAGECONFIG_append = " networkd"
PACKAGECONFIG_remove = "machined hibernate ldconfig binfmt backlight quotacheck localed kdbus ima smack polkit"
FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"

RRECOMMENDS_${PN} += "obmc-targets"
FILES_${PN} += "${libdir}/systemd/network/default.network"

EXTRA_OECONF += " --disable-hwdb"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${libdir}/systemd/network/

        #TODO Remove after this issue is resolved
        #https://github.com/openbmc/openbmc/issues/152
        ln -s /dev/null ${D}/etc/systemd/system/systemd-hwdb-update.service
}
