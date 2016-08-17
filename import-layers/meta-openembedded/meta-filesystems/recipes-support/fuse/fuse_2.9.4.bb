SUMMARY = "Implementation of a fully functional filesystem in a userspace program"
DESCRIPTION = "FUSE (Filesystem in Userspace) is a simple interface for userspace \
               programs to export a virtual filesystem to the Linux kernel. FUSE \
               also aims to provide a secure method for non privileged users to \
               create and mount their own filesystem implementations. \
              "
HOMEPAGE = "http://fuse.sf.net"
SECTION = "libs"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "${SOURCEFORGE_MIRROR}/fuse/fuse-${PV}.tar.gz \
           file://gold-unversioned-symbol.patch \
           file://aarch64.patch \
           file://0001-fuse-fix-the-return-value-of-help-option.patch \
           file://fuse.conf \
"
SRC_URI[md5sum] = "ecb712b5ffc6dffd54f4a405c9b372d8"
SRC_URI[sha256sum] = "6be9c0bff6af8c677414935f31699ea5a7f8f5f791cfa5205be02ea186b97ce1"

inherit autotools pkgconfig update-rc.d systemd

INITSCRIPT_NAME = "fuse"

SYSTEMD_SERVICE_${PN} = ""

DEPENDS = "gettext-native"

PACKAGES =+ "fuse-utils-dbg fuse-utils libulockmgr libulockmgr-dev libulockmgr-dbg"

# Fusermount requires features from the util-linux version of mount.
RDEPENDS_${PN} += "util-linux-mount"

RRECOMMENDS_${PN} = "kernel-module-fuse libulockmgr fuse-utils"

FILES_${PN} += "${libdir}/libfuse.so.*"
FILES_${PN}-dev += "${libdir}/libfuse*.la"

FILES_libulockmgr = "${libdir}/libulockmgr.so.*"
FILES_libulockmgr-dev += "${libdir}/libulock*.la"
FILES_libulockmgr-dbg += "${libdir}/.debug/libulock*"

# Forbid auto-renaming to libfuse-utils
FILES_fuse-utils = "${bindir} ${base_sbindir}"
FILES_fuse-utils-dbg = "${bindir}/.debug ${base_sbindir}/.debug"
DEBIAN_NOAUTONAME_fuse-utils = "1"
DEBIAN_NOAUTONAME_fuse-utils-dbg = "1"

do_install_append() {
    rm -rf ${D}${base_prefix}/dev

    # systemd class remove the sysv_initddir only if systemd_system_unitdir
    # contains anything, but it's not needed if sysvinit is not in DISTRO_FEATURES
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'false', 'true', d)}; then
        rm -rf ${D}${sysconfdir}/init.d/
    fi

    # Install systemd related configuration file
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/modules-load.d
        install -m 0644 ${WORKDIR}/fuse.conf ${D}${sysconfdir}/modules-load.d
    fi
}
