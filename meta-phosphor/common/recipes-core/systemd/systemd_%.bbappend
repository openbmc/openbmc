FILES_${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG_append = " networkd coredump journald-dbus"
PACKAGECONFIG_remove = "machined hibernate ldconfig binfmt backlight localed \
                        quotacheck kdbus ima smack polkit logind bootchart utmp \
                        manpages"
FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"
SRC_URI += "file://service-restart-policy.conf"
SRC_URI += "file://0001-sd-bus-Don-t-automatically-add-ObjectManager.patch"
SRC_URI += "file://0001-Export-message_append_cmdline.patch"
SRC_URI += "file://0002-systemd-Make-pam-compile-shared-library.patch"
SRC_URI += "file://0003-basic-Factor-out-string-checking-from-name_to_prefix.patch"
SRC_URI += "file://0004-basic-Use-path-escaping-when-mangling-path-instances.patch"
#TODO upstream the below patch via below issue
#https://github.com/openbmc/openbmc/issues/2016
SRC_URI += "file://0005-dont-return-error-if-unable-to-create-network-namespace.patch"
SRC_URI += "file://0006-journal-Create-journald-dbus-object.patch"

# Extra configuration for journald-dbus patch
PACKAGECONFIG[journald-dbus] = "--enable-journald-dbus,--disable-journald-dbus"
FILES_${PN} += "${datadir}/dbus-1/system.d/org.freedesktop.journal1.conf"

SRC_URI += "file://0007-journal-Add-Synchronize-dbus-method.patch"
SRC_URI_append_df-obmc-ubi-fs = " file://software.conf"

SRC_URI += "file://0001-watchdog-allow-a-device-path-to-be-specified.patch"
SRC_URI += "file://0002-core-Add-WatchdogDevice-config-option-and-implement-.patch"

RRECOMMENDS_${PN} += "obmc-targets"
FILES_${PN} += "${libdir}/systemd/network/default.network"
FILES_${PN} += "${libdir}/systemd/system.conf.d/service-restart-policy.conf"

EXTRA_OECONF += " --disable-hwdb"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${libdir}/systemd/network/
        install -m 644 -D ${WORKDIR}/service-restart-policy.conf ${D}${libdir}/systemd/system.conf.d/service-restart-policy.conf

        #TODO Remove after this issue is resolved
        #https://github.com/openbmc/openbmc/issues/152
        ln -s /dev/null ${D}/etc/systemd/system/systemd-hwdb-update.service
}

do_install_append_df-obmc-ubi-fs() {
        # /tmp/images is the software image upload directory.
        # It should not be deleted since it is watched by the Image Manager
        # for new images.
        install -m 0644 ${WORKDIR}/software.conf ${D}${exec_prefix}/lib/tmpfiles.d/
}
