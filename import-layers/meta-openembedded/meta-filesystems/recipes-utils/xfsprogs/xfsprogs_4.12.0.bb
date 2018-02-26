SUMMARY = "XFS Filesystem Utilities"
HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_libhandle = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=102f7fec3d53c7c8f0b7baf9bf9d76a8"
DEPENDS = "util-linux util-linux-native"
SRC_URI = "https://www.kernel.org/pub/linux/utils/fs/xfs/xfsprogs/${BP}.tar.xz \
           file://0001-xfsprogs-remove-double-underscore-integer-types.patch \
           file://0001-xfs-remove-double-underscore-integer-types.patch \
           file://remove_flags_from_build_flags.patch \
           file://xfsprogs-4.12.0-fix_musl.patch \
           "
SRC_URI[md5sum] = "e348b0e1d7bd1cd82d64b91ff37e727e"
SRC_URI[sha256sum] = "b330ad8d737f4152ae511580102e2fc49212bb51dfb4b614084344abae46d0df"

inherit autotools-brokensep

PACKAGES =+ "${PN}-fsck ${PN}-mkfs ${PN}-repair libhandle"

DEPENDS += "util-linux"

RDEPENDS_${PN} = "${PN}-fsck ${PN}-mkfs ${PN}-repair"

FILES_${PN}-fsck = "${base_sbindir}/fsck.xfs"
FILES_${PN}-mkfs = "${base_sbindir}/mkfs.xfs"
FILES_${PN}-repair = "${base_sbindir}/xfs_repair"

FILES_libhandle = "${base_libdir}/libhandle${SOLIBS}"

EXTRA_OECONF = "--enable-gettext=no \
                INSTALL_USER=root \
                INSTALL_GROUP=root \
                ac_cv_header_aio_h=yes \
                ac_cv_lib_rt_lio_listio=yes \
"

DISABLE_STATIC = ""
EXTRA_AUTORECONF += "-I ${S}/m4 --exclude=autoheader"

PACKAGECONFIG ??= "readline blkid"

PACKAGECONFIG[readline] = "--enable-readline=yes,--enable-readline=no,readline"
PACKAGECONFIG[blkid] = "--enable-blkid=yes,--enable-blkid=no,util-linux"

export DEBUG="-DNDEBUG"
export BUILD_VERBOSE="1"
export tagname="CC"

EXTRA_OEMAKE = "DIST_ROOT='${D}'"

do_configure_prepend () {
    export BUILD_CC="${BUILD_CC} ${BUILD_CFLAGS}"
    # Prevent Makefile from calling configure without arguments,
    # when do_configure gets called for a second time.
    rm -f ${B}/include/builddefs ${B}/include/platform_defs.h ${B}/configure
    # Recreate configure script.
    oe_runmake configure
}

do_install_append() {
        oe_runmake 'DESTDIR=${D}' install-dev
        rm ${D}${libdir}/*.la
        rmdir --ignore-fail-on-non-empty ${D}${libdir}
}
