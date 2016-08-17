SECTION = "utils"
DESCRIPTION = "LVM2 is a set of utilities to manage logical volumes in Linux."
DEPENDS = "udev"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=fbc093901857fcd118f065f900982c24"

SRC_URI = "ftp://sources.redhat.com/pub/lvm2/old/LVM2.${PV}.tgz \
           file://lvm.conf \
           file://0001-implement-libc-specific-_reopen_stream.patch \
           file://0002-use-PTHREAD_MUTEX_RECURSIVE-instead-of-PTHREAD_MUTEX.patch \
           file://0003-Guard-use-of-mallinfo-with-__GLIBC__.patch \
           file://0004-include-fcntl.h-for-O_-defines-and-fcntl-signature.patch \
           "

S = "${WORKDIR}/LVM2.${PV}"

inherit autotools-brokensep pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"

PACKAGECONFIG[readline] = "--enable-readline,--disable-readline,readline"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

# Unset user/group to unbreak install.
EXTRA_OECONF = "--with-user= \
                --with-group= \
                --disable-o_direct \
                --enable-realtime \
                --enable-applib \
                --enable-cmdlib \
                --enable-udev_sync \
                --enable-udev_rules \
                --enable-pkgconfig \
                --enable-dmeventd \
                --enable-lvmetad \
                --with-udev-prefix= \
                --with-usrlibdir=${libdir} \
                --with-systemdsystemunitdir=${systemd_system_unitdir} \
"

do_install_append() {
    # Install machine specific configuration file
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

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "lvm2-monitor.service dm-event.socket dm-event.service lvm2-lvmetad.socket \
                         lvm2-pvscan@.service blk-availability.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

FILES_${PN} += "${libdir}/device-mapper/*.so ${base_libdir}/udev"
FILES_${PN}-dbg += "${libdir}/device-mapper/.debug"

RDEPENDS_${PN} = "bash"

CONFFILES_${PN} += "${sysconfdir}/lvm/lvm.conf"
