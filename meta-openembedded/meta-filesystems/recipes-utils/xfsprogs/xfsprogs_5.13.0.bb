SUMMARY = "XFS Filesystem Utilities"
HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "GPLv2 & LGPLv2.1"
LICENSE:libhandle = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"
DEPENDS = "util-linux util-linux-native"
SRC_URI = "https://www.kernel.org/pub/linux/utils/fs/xfs/xfsprogs/${BP}.tar.xz \
           file://remove_flags_from_build_flags.patch \
           file://0001-Check-for-MAP_SYNC-in-sys-mman.h.patch \
           file://0002-include-include-xfs-linux.h-after-sys-mman.h.patch \
           file://0001-support-usrmerge.patch \
           "
SRC_URI[sha256sum] = "4e142d4babe086adf9016d8c606c805829da08e46389a4433f40346204f90cdb"
inherit autotools-brokensep

PACKAGES =+ "${PN}-fsck ${PN}-mkfs ${PN}-repair libhandle"

DEPENDS += "util-linux libinih"

RDEPENDS:${PN} = "${PN}-fsck ${PN}-mkfs ${PN}-repair"

FILES:${PN}-fsck = "${base_sbindir}/fsck.xfs"
FILES:${PN}-mkfs = "${base_sbindir}/mkfs.xfs"
FILES:${PN}-repair = "${base_sbindir}/xfs_repair"

FILES:libhandle = "${base_libdir}/libhandle${SOLIBS}"

EXTRA_OECONF = "--enable-gettext=no \
                --enable-scrub=no \
                INSTALL_USER=root \
                INSTALL_GROUP=root \
                ac_cv_header_aio_h=yes \
                ac_cv_lib_rt_lio_listio=yes \
                OPTIMIZER='${SELECTED_OPTIMIZATION}' \
"

DISABLE_STATIC = ""
EXTRA_AUTORECONF += "-I ${S}/m4 --exclude=autoheader"

PACKAGECONFIG ??= "blkid"

PACKAGECONFIG[blkid] = "--enable-blkid=yes,--enable-blkid=no,util-linux"

export DEBUG="-DNDEBUG"
export BUILD_VERBOSE="1"
export tagname="CC"

EXTRA_OEMAKE = "DIST_ROOT='${D}'"

do_configure () {
    export BUILD_CC="${BUILD_CC} ${BUILD_CFLAGS}"
    # Prevent Makefile from calling configure without arguments,
    # when do_configure gets called for a second time.
    rm -f ${B}/include/builddefs ${B}/include/platform_defs.h ${B}/configure
    # Recreate configure script.
    oe_runmake configure
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf
}

do_install:append() {
        oe_runmake 'DESTDIR=${D}' install-dev
        rm ${D}${libdir}/*.la
        rmdir --ignore-fail-on-non-empty ${D}${libdir}

        if [ ${libdir} != ${base_libdir} ];then
            ln -sf -r ${D}${libdir}/libhandle.a ${D}${base_libdir}/libhandle.a
            ln -sf -r ${D}${base_libdir}/libhandle.so ${D}${libdir}/libhandle.so
        fi
}
