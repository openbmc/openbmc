DESCRIPTION = "The NTFS-3G driver is an open source, freely available NTFS driver for Linux with read and write support."
HOMEPAGE = "http://www.ntfs-3g.org/"
DEPENDS = "fuse libgcrypt"
PROVIDES = "ntfsprogs ntfs-3g"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a"

SRC_URI = "http://tuxera.com/opensource/ntfs-3g_ntfsprogs-${PV}.tgz \
           file://0001-libntfs-3g-Makefile.am-fix-install-failed-while-host.patch \
"
S = "${WORKDIR}/ntfs-3g_ntfsprogs-${PV}"
SRC_URI[md5sum] = "90da343e78877d388eb34cefae6799ae"
SRC_URI[sha256sum] = "55b883aa05d94b2ec746ef3966cb41e66bed6db99f22ddd41d1b8b94bb202efb"

UPSTREAM_CHECK_URI = "https://www.tuxera.com/community/open-source-ntfs-3g/"
UPSTREAM_CHECK_REGEX = "ntfs-3g_ntfsprogs-(?P<pver>\d+(\.\d+)+)\.tgz"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[uuid] = "--with-uuid,--without-uuid,util-linux"

# required or it calls ldconfig at install step
EXTRA_OEMAKE = "LDCONFIG=echo"

PACKAGES =+ "ntfs-3g ntfsprogs libntfs-3g"

FILES:ntfs-3g = "${base_sbindir}/*.ntfs-3g ${bindir}/ntfs-3g* ${base_sbindir}/mount.ntfs"
RDEPENDS:ntfs-3g += "fuse"
RRECOMMENDS:ntfs-3g = "util-linux-mount"

FILES:ntfsprogs = "${base_sbindir}/* ${bindir}/* ${sbindir}/*"
FILES:libntfs-3g = "${libdir}/*${SOLIBS}"

do_install:append() {
    # Standard mount will execute the program /sbin/mount.TYPE when called.
    # Add a symbolic link to let mount find ntfs.
    ln -sf mount.ntfs-3g ${D}${base_sbindir}/mount.ntfs
    rmdir ${D}${libdir}/ntfs-3g

    # Handle when usrmerge is in effect. Some files are installed to /sbin
    # regardless of the value of ${base_sbindir}.
    if [ "${base_sbindir}" != /sbin ] && [ -d ${D}/sbin ]; then
        mkdir -p ${D}${base_sbindir}
        mv ${D}/sbin/* ${D}${base_sbindir}
        rmdir ${D}/sbin
    fi
}

# Satisfy the -dev runtime dependency
ALLOW_EMPTY:${PN} = "1"
