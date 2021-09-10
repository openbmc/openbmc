require lvm2.inc

SRC_URI += " \
            file://0001-fix-command-bin-findmnt-bin-lsblk-bin-sort-not-found.patch \
           "

DEPENDS += "autoconf-archive-native"

inherit multilib_script

MULTILIB_SCRIPTS = "${PN}:${sysconfdir}/lvm/lvm.conf"

CACHED_CONFIGUREVARS += "MODPROBE_CMD=${base_sbindir}/modprobe"

do_install:append() {
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
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'dmeventd', 'lvm2-monitor.service dm-event.socket dm-event.service', '', d)} \
                         blk-availability.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF:append:class-nativesdk = " --with-confdir=${sysconfdir}"

DEPENDS += "util-linux"
LVM2_PACKAGECONFIG:append:class-target = " \
    udev \
"
PACKAGECONFIG[udev] = "--enable-udev_sync --enable-udev_rules --with-udevdir=${nonarch_base_libdir}/udev/rules.d,--disable-udev_sync --disable-udev_rules,udev,${PN}-udevrules"

PACKAGES =+ "libdevmapper"
FILES:libdevmapper = " \
    ${libdir}/libdevmapper.so.* \
    ${sbindir}/dmsetup \
    ${sbindir}/dmstats \
"

FILES:${PN} += " \
    ${libdir}/device-mapper/*.so \
    ${systemd_system_unitdir}/lvm2-pvscan@.service \
"

FILES:${PN}-scripts = " \
    ${sbindir}/blkdeactivate \
    ${sbindir}/fsadm \
    ${sbindir}/lvmconf \
    ${sbindir}/lvmdump \
"
# Specified explicitly for the udev rules, just in case that it does not get picked
# up automatically:
FILES:${PN}-udevrules = "${nonarch_base_libdir}/udev/rules.d"
RDEPENDS:${PN}-udevrules = "libdevmapper"
RDEPENDS:${PN}:append:class-target = " libdevmapper"
RDEPENDS:${PN}:append:class-nativesdk = " libdevmapper"

RDEPENDS:${PN}-scripts = "${PN} (= ${EXTENDPKGV}) \
                          bash \
                          util-linux-lsblk \
                          util-linux-findmnt \
                          coreutils \
"
RRECOMMENDS:${PN}:class-target = "${PN}-scripts (= ${EXTENDPKGV})"

CONFFILES:${PN} += "${sysconfdir}/lvm/lvm.conf"

SYSROOT_PREPROCESS_FUNCS:append = " remove_libdevmapper_sysroot_preprocess"
remove_libdevmapper_sysroot_preprocess() {
    rm -f ${SYSROOT_DESTDIR}${libdir}/libdevmapper.so* \
       ${SYSROOT_DESTDIR}${sbindir}/dmsetup \
       ${SYSROOT_DESTDIR}${sbindir}/dmstats \
       ${SYSROOT_DESTDIR}${includedir}/libdevmapper.h \
       ${SYSROOT_DESTDIR}${libdir}/pkgconfig/devmapper.pc
}

BBCLASSEXTEND = "native nativesdk"
