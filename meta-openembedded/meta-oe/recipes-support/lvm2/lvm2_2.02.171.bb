require lvm2.inc

SRC_URI[md5sum] = "153b7bb643eb26073274968e9026fa8f"
SRC_URI[sha256sum] = "b815a711a2fabaa5c3dc1a4a284df0268bf0f325f0fc0f5c9530c9bbb54b9964"

SRC_URI += "file://0001-explicitly-do-not-install-libdm.patch"

DEPENDS += "autoconf-archive-native"

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
                         ${@bb.utils.contains('PACKAGECONFIG', 'lvmetad', 'lvm2-lvmetad.socket lvm2-pvscan@.service', '', d)} \
                         blk-availability.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

FILES_${PN} += "${libdir}/device-mapper/*.so"
FILES_${PN}-scripts = " \
    ${sbindir}/blkdeactivate \
    ${sbindir}/fsadm \
    ${sbindir}/lvmconf \
    ${sbindir}/lvmdump \
"
# Specified explicitly for the udev rules, just in case that it does not get picked
# up automatically:
FILES_${PN}-udevrules = "${nonarch_base_libdir}/udev/rules.d"
RDEPENDS_${PN}_append_class-target = " libdevmapper"

RDEPENDS_${PN}-scripts = "${PN} (= ${EXTENDPKGV}) bash"
RRECOMMENDS_${PN}_class-target = "${PN}-scripts (= ${EXTENDPKGV})"

CONFFILES_${PN} += "${sysconfdir}/lvm/lvm.conf"

BBCLASSEXTEND = "native nativesdk"
