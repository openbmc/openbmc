require lvm2.inc

SRC_URI += " \
            file://tweak-for-lvmdbusd.patch \
           "

DEPENDS += "autoconf-archive-native"

inherit multilib_script python3native

MULTILIB_SCRIPTS = "${PN}:${sysconfdir}/lvm/lvm.conf"

CACHED_CONFIGUREVARS += "MODPROBE_CMD=${base_sbindir}/modprobe"

do_install:append() {
    # Install machine specific configuration file
    install -d ${D}${sysconfdir}/lvm
    install -m 0644 ${UNPACKDIR}/lvm.conf ${D}${sysconfdir}/lvm/lvm.conf
    sed -i -e 's:@libdir@:${libdir}:g' ${D}${sysconfdir}/lvm/lvm.conf
    # We don't want init scripts/systemd units for native SDK utilities
    if [ "${PN}" != "nativesdk-lvm2" ]; then
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
            oe_runmake 'DESTDIR=${D}' install install_systemd_units
            sed -i -e 's:/usr/bin/true:${base_bindir}/true:g' ${D}${systemd_system_unitdir}/blk-availability.service
        else
            oe_runmake 'DESTDIR=${D}' install install_initscripts
            mv ${D}${sysconfdir}/rc.d/init.d ${D}${sysconfdir}/init.d
            rm -rf ${D}${sysconfdir}/rc.d
        fi
    fi

    # following files only exist when package config `dbus` enabled
    sed -i -e '1s,#!.*python.*,#!${USRBINPATH}/env python3,' \
        ${D}${sbindir}/lvmdbusd \
        ${D}${PYTHON_SITEPACKAGES_DIR}/lvmdbusd/lvmdb.py \
        ${D}${PYTHON_SITEPACKAGES_DIR}/lvmdbusd/lvm_shell_proxy.py \
    || true
}

PACKAGE_BEFORE_PN = "${PN}-scripts"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'dmeventd', 'lvm2-monitor.service dm-event.socket dm-event.service', '', d)} \
                         ${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'lvm2-lvmdbusd.service', '', d)} \
                         blk-availability.service \
                         "
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF:append:class-nativesdk = " --with-confdir=${sysconfdir}"

DEPENDS += "util-linux"
LVM2_PACKAGECONFIG:append:class-target = " \
    udev \
"

PACKAGECONFIG[dbus] = "--enable-dbus-service,--disable-dbus-service,,python3-dbus python3-pyudev"
PACKAGECONFIG[udev] = "--enable-udev_sync --enable-udev_rules --with-udevdir=${nonarch_base_libdir}/udev/rules.d,--disable-udev_sync --disable-udev_rules,udev,"

PACKAGES =+ "libdevmapper"
FILES:libdevmapper = " \
    ${libdir}/libdevmapper.so.* \
    ${sbindir}/dmsetup \
    ${sbindir}/dmstats \
    ${nonarch_base_libdir}/udev/rules.d/10-dm.rules \
    ${nonarch_base_libdir}/udev/rules.d/13-dm-disk.rules \
    ${nonarch_base_libdir}/udev/rules.d/95-dm-notify.rules \
"

FILES:${PN} += " \
    ${libdir}/device-mapper/*.so \
    ${systemd_system_unitdir} \
    ${PYTHON_SITEPACKAGES_DIR}/lvmdbusd \
    ${datadir}/dbus-1/system-services/com.redhat.lvmdbus1.service \
    ${nonarch_base_libdir}/udev/rules.d/11-dm-lvm.rules \
    ${nonarch_base_libdir}/udev/rules.d/69-dm-lvm.rules \
"
# Remove /lib/udev from FILES:${PN} so that any new rules files that are added
# upstream will have to be explicitly added to either FILES:${PN} or
# FILES:libdevmapper.
FILES:${PN}:remove = "${nonarch_base_libdir}/udev"

FILES:${PN}-scripts = " \
    ${sbindir}/blkdeactivate \
    ${sbindir}/fsadm \
    ${sbindir}/lvmconf \
    ${sbindir}/lvmdump \
"

RDEPENDS:${PN} = "bash"
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
