FILES_${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG_append = " networkd"
PACKAGECONFIG_remove = "machined hibernate ldconfig binfmt backlight localed \
                        quotacheck kdbus ima smack polkit logind bootchart utmp"
FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"
SRC_URI += "file://shutdown-watchdog.conf"
SRC_URI += "file://0001-Export-message_append_cmdline.patch"
SRC_URI += "file://0002-systemd-Make-pam-compile-shared-library.patch"
SRC_URI += "file://0003-basic-Factor-out-string-checking-from-name_to_prefix.patch"
SRC_URI += "file://0004-basic-Use-path-escaping-when-mangling-path-instances.patch"

RRECOMMENDS_${PN} += "obmc-targets"
FILES_${PN} += "${libdir}/systemd/network/default.network"
FILES_${PN} += "${libdir}/systemd/system.conf.d/shutdown-watchdog.conf"

EXTRA_OECONF += " --disable-hwdb"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${libdir}/systemd/network/
        install -m 644 -D ${WORKDIR}/shutdown-watchdog.conf ${D}${libdir}/systemd/system.conf.d/shutdown-watchdog.conf

        #TODO Remove after this issue is resolved
        #https://github.com/openbmc/openbmc/issues/152
        ln -s /dev/null ${D}/etc/systemd/system/systemd-hwdb-update.service
}
