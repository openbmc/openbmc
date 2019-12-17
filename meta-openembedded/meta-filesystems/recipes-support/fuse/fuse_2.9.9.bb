SUMMARY = "Implementation of a fully functional filesystem in a userspace program"
DESCRIPTION = "FUSE (Filesystem in Userspace) is a simple interface for userspace \
               programs to export a virtual filesystem to the Linux kernel. FUSE \
               also aims to provide a secure method for non privileged users to \
               create and mount their own filesystem implementations. \
              "
HOMEPAGE = "https://github.com/libfuse/libfuse"
SECTION = "libs"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/libfuse/libfuse/releases/download/${BP}/${BP}.tar.gz \
           file://gold-unversioned-symbol.patch \
           file://aarch64.patch \
           file://0001-fuse-fix-the-return-value-of-help-option.patch \
           file://fuse.conf \
"
SRC_URI[md5sum] = "8000410aadc9231fd48495f7642f3312"
SRC_URI[sha256sum] = "d0e69d5d608cc22ff4843791ad097f554dd32540ddc9bed7638cc6fea7c1b4b5"

UPSTREAM_CHECK_URI = "https://github.com/libfuse/libfuse/releases"
UPSTREAM_CHECK_REGEX = "fuse\-(?P<pver>2(\.\d+)+).tar.gz"

inherit autotools pkgconfig update-rc.d systemd

INITSCRIPT_NAME = "fuse"
INITSCRIPT_PARAMS = "start 3 S . stop 20 0 6 ."

SYSTEMD_SERVICE_${PN} = ""

DEPENDS = "gettext-native"

PACKAGES =+ "fuse-utils libulockmgr libulockmgr-dev"

RPROVIDES_${PN}-dbg += "fuse-utils-dbg libulockmgr-dbg"

RRECOMMENDS_${PN}_class-target = "kernel-module-fuse libulockmgr fuse-utils"

FILES_${PN} += "${libdir}/libfuse.so.*"
FILES_${PN}-dev += "${libdir}/libfuse*.la"

FILES_libulockmgr = "${libdir}/libulockmgr.so.*"
FILES_libulockmgr-dev += "${libdir}/libulock*.la"

# Forbid auto-renaming to libfuse-utils
FILES_fuse-utils = "${bindir} ${base_sbindir}"
DEBIAN_NOAUTONAME_fuse-utils = "1"
DEBIAN_NOAUTONAME_${PN}-dbg = "1"

do_configure_prepend() {
    # Make this explicit so overriding base_sbindir propagates properly.
    export MOUNT_FUSE_PATH="${base_sbindir}"
}

do_install_append() {
    rm -rf ${D}/dev

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

do_install_append_class-nativesdk() {
    install -d ${D}${sysconfdir}
    mv ${D}/etc/* ${D}${sysconfdir}/
    rmdir ${D}/etc
}

BBCLASSEXTEND = "native nativesdk"
