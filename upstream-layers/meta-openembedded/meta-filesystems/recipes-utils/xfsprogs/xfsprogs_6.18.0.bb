SUMMARY = "XFS Filesystem Utilities"
HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LICENSE:libhandle = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"

SRC_URI = "https://www.kernel.org/pub/linux/utils/fs/xfs/xfsprogs/${BP}.tar.xz \
           file://remove_flags_from_build_flags.patch \
           file://0001-include-include-xfs-linux.h-after-sys-mman.h.patch \
           file://0002-configure-Use-AC_SYS_LARGERFILE-autoconf-macro.patch \
           file://0003-doc-man-support-reproducible-builds.patch \
           "

SRC_URI[sha256sum] = "3a6dc7b1245ce9bccd197bab00691f1b190bd3694d3ccc301d21b83afc133199"

inherit autotools-brokensep pkgconfig

DEPENDS = "util-linux util-linux-native libinih liburcu"

PACKAGES =+ "${PN}-fsck ${PN}-mkfs ${PN}-repair libhandle"

RDEPENDS:${PN} = "${PN}-fsck ${PN}-mkfs ${PN}-repair python3-core bash"

FILES:${PN}-fsck = "${sbindir}/fsck.xfs"
FILES:${PN}-mkfs = "${sbindir}/mkfs.xfs"
FILES:${PN}-repair = "${sbindir}/xfs_repair"

FILES:libhandle = "${libdir}/libhandle${SOLIBS}"

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

export DEBUG = "-DNDEBUG"
export BUILD_VERBOSE = "1"
export tagname = "CC"

EXTRA_OEMAKE = "DIST_ROOT='${D}'"

do_configure() {
    export BUILD_CC="${BUILD_CC} ${BUILD_CFLAGS}"
    # Prevent Makefile from calling configure without arguments,
    # when do_configure gets called for a second time.
    rm -f ${B}/include/builddefs ${B}/configure
    # Recreate configure script.
    oe_runmake configure
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf
}

do_install:append() {
    oe_runmake 'DESTDIR=${D}' install-dev
}
