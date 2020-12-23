SUMMARY = "XFS Filesystem Utilities"
HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_libhandle = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"
DEPENDS = "util-linux util-linux-native"
SRC_URI = "https://www.kernel.org/pub/linux/utils/fs/xfs/xfsprogs/${BP}.tar.xz \
           file://remove_flags_from_build_flags.patch \
           file://0001-Check-for-MAP_SYNC-in-sys-mman.h.patch \
           file://0002-include-include-xfs-linux.h-after-sys-mman.h.patch \
           file://0001-support-usrmerge.patch \
           "
SRC_URI[sha256sum] = "bc5c805596bc609a18dc1f1b4ed6a2821dba9f47408ec00e7799ceea1b2097f1"
inherit autotools-brokensep

PACKAGES =+ "${PN}-fsck ${PN}-mkfs ${PN}-repair libhandle"

DEPENDS += "util-linux"

RDEPENDS_${PN} = "${PN}-fsck ${PN}-mkfs ${PN}-repair"

FILES_${PN}-fsck = "${base_sbindir}/fsck.xfs"
FILES_${PN}-mkfs = "${base_sbindir}/mkfs.xfs"
FILES_${PN}-repair = "${base_sbindir}/xfs_repair"

FILES_libhandle = "${base_libdir}/libhandle${SOLIBS}"

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

do_install_append() {
        oe_runmake 'DESTDIR=${D}' install-dev
        rm ${D}${libdir}/*.la
        rmdir --ignore-fail-on-non-empty ${D}${libdir}

        if [ ${libdir} != ${base_libdir} ];then
            ln -sf -r ${D}${libdir}/libhandle.a ${D}${base_libdir}/libhandle.a
            ln -sf -r ${D}${base_libdir}/libhandle.so ${D}${libdir}/libhandle.so
        fi
}
