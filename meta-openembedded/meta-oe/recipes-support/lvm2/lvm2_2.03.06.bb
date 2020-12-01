require lvm2.inc

SRC_URI += " \
            file://0001-dev-hdc-open-failed-No-medium-found-will-print-out-i.patch \
            file://0001-fix-command-bin-findmnt-bin-lsblk-bin-sort-not-found.patch \
           "

DEPENDS += "autoconf-archive-native"

inherit multilib_script

MULTILIB_SCRIPTS = "${PN}:${sysconfdir}/lvm/lvm.conf"

CACHED_CONFIGUREVARS += "MODPROBE_CMD=${base_sbindir}/modprobe"

do_install_append() {
    # Install machine specific configuration file
    install -d ${D}${sysconfdir}/lvm
    install -m 0644 ${WORKDIR}/lvm.conf ${D}${sysconfdir}/lvm/lvm.conf
    sed -i -e 's:@libdir@:${libdir}:g' ${D}${sysconfdir}/lvm/lvm.conf
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        oe_runmake 'DESTDIR=${D}' install install_systemd_units
        sed -i -e 's:/usr/bin/true:${base_bindir}/true:g' ${D}${systemd_system_unitdir}/blk-availability.service
    else
        oe_runmake 'DESTDIR=${D}' install install_initscripts
        mv ${D}${sysconfdir}/rc.d/init.d ${D}${sysconfdir}/init.d
        rm -rf ${D}${sysconfdir}/rc.d
    fi
}

PACKAGE_BEFORE_PN = "${PN}-scripts ${PN}-udevrules"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'dmeventd', 'lvm2-monitor.service dm-event.socket dm-event.service', '', d)} \
                         blk-availability.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF_append_class-nativesdk = " --with-confdir=${sysconfdir}"

DEPENDS += "util-linux"
LVM2_PACKAGECONFIG_append_class-target = " \
    udev \
"
PACKAGECONFIG[udev] = "--enable-udev_sync --enable-udev_rules --with-udevdir=${nonarch_base_libdir}/udev/rules.d,--disable-udev_sync --disable-udev_rules,udev,${PN}-udevrules"

PACKAGES =+ "libdevmapper"
FILES_libdevmapper = " \
    ${libdir}/libdevmapper.so.* \
    ${sbindir}/dmsetup \
    ${sbindir}/dmstats \
"

FILES_${PN} += " \
    ${libdir}/device-mapper/*.so \
    ${systemd_system_unitdir}/lvm2-pvscan@.service \
"

FILES_${PN}-scripts = " \
    ${sbindir}/blkdeactivate \
    ${sbindir}/fsadm \
    ${sbindir}/lvmconf \
    ${sbindir}/lvmdump \
"
# Specified explicitly for the udev rules, just in case that it does not get picked
# up automatically:
FILES_${PN}-udevrules = "${nonarch_base_libdir}/udev/rules.d"
RDEPENDS_${PN}-udevrules = "libdevmapper"
RDEPENDS_${PN}_append_class-target = " libdevmapper"
RDEPENDS_${PN}_append_class-nativesdk = " libdevmapper"

RDEPENDS_${PN}-scripts = "${PN} (= ${EXTENDPKGV}) \
                          bash \
                          util-linux-lsblk \
                          util-linux-findmnt \
                          coreutils \
"
RRECOMMENDS_${PN}_class-target = "${PN}-scripts (= ${EXTENDPKGV})"

CONFFILES_${PN} += "${sysconfdir}/lvm/lvm.conf"

SYSROOT_PREPROCESS_FUNCS_append = " remove_libdevmapper_sysroot_preprocess"
remove_libdevmapper_sysroot_preprocess() {
    rm -f ${SYSROOT_DESTDIR}${libdir}/libdevmapper.so* \
       ${SYSROOT_DESTDIR}${sbindir}/dmsetup \
       ${SYSROOT_DESTDIR}${sbindir}/dmstats \
       ${SYSROOT_DESTDIR}${includedir}/libdevmapper.h \
       ${SYSROOT_DESTDIR}${libdir}/pkgconfig/devmapper.pc
}

BBCLASSEXTEND = "native nativesdk"
