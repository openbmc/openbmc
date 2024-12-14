DESCRIPTION = "The NTFS-3G driver is an open source, freely available NTFS driver for Linux with read and write support."
HOMEPAGE = "http://www.ntfs-3g.org/"
DEPENDS = "fuse libgcrypt"
PROVIDES = "ntfsprogs ntfs-3g"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a"

SRC_URI = "http://tuxera.com/opensource/ntfs-3g_ntfsprogs-${PV}.tgz \
           file://0001-libntfs-3g-Makefile.am-fix-install-failed-while-host.patch \
           file://0001-unistr.c-Fix-use-after-free-in-ntfs_uppercase_mbs.patch \
"
S = "${WORKDIR}/ntfs-3g_ntfsprogs-${PV}"
SRC_URI[sha256sum] = "f20e36ee68074b845e3629e6bced4706ad053804cbaf062fbae60738f854170c"

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

CVE_PRODUCT = "tuxera:ntfs-3g"
