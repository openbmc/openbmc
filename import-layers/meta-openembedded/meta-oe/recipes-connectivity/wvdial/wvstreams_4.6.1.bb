HOMEPAGE = "http://alumnit.ca/wiki/index.php?page=WvStreams"
SUMMARY = "WvStreams is a network programming library in C++"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "zlib openssl (>= 0.9.8) dbus readline"
DEPENDS_append_libc-musl = " argp-standalone libexecinfo"

SRC_URI = "http://${BPN}.googlecode.com/files/${BP}.tar.gz \
           file://04_signed_request.diff \
           file://05_gcc.diff \
           file://06_gcc-4.7.diff \
           file://07_buildflags.diff \
           file://gcc-6.patch \
           file://argp.patch \
           file://0001-Check-for-limits.h-during-configure.patch \
           file://0002-wvtask-Dont-use-ucontext-on-non-glibc-systems.patch \
           file://0003-wvtask-Check-for-HAVE_LIBC_STACK_END-only-on-glibc-s.patch \
           file://0004-wvcrash-Replace-use-of-basename-API.patch \
           file://0005-check-for-libexecinfo-during-configure.patch \
           file://0001-build-fix-parallel-make.patch \
           file://0002-wvrules.mk-Use-_DEFAULT_SOURCE.patch \
           "

SRC_URI[md5sum] = "2760dac31a43d452a19a3147bfde571c"
SRC_URI[sha256sum] = "8403f5fbf83aa9ac0c6ce15d97fd85607488152aa84e007b7d0621b8ebc07633"

inherit autotools-brokensep pkgconfig

TARGET_CFLAGS_append = " -fno-tree-dce -fno-optimize-sibling-calls"

LDFLAGS_append = " -Wl,-rpath-link,${CROSS_DIR}/${TARGET_SYS}/lib"

EXTRA_OECONF = " --without-tcl --without-qt --without-pam --without-valgrind"

PACKAGES_prepend = "libuniconf libuniconf-dbg "
PACKAGES_prepend = "uniconfd uniconfd-dbg "
PACKAGES_prepend = "libwvstreams-base libwvstreams-base-dbg "
PACKAGES_prepend = "libwvstreams-extras libwvstreams-extras-dbg "
PACKAGES_prepend = "${PN}-valgrind "

FILES_libuniconf     = "${libdir}/libuniconf.so.*"
FILES_libuniconf-dbg = "${libdir}/.debug/libuniconf.so.*"

FILES_uniconfd     = "${sbindir}/uniconfd ${sysconfdir}/uniconf.conf ${localstatedir}/uniconf"
FILES_uniconfd-dbg = "${sbindir}/.debug/uniconfd"

FILES_libwvstreams-base     = "${libdir}/libwvutils.so.*"
FILES_libwvstreams-base-dbg = "${libdir}/.debug/libwvutils.so.*"

FILES_libwvstreams-extras     = "${libdir}/libwvbase.so.* ${libdir}/libwvstreams.so.*"
FILES_libwvstreams-extras-dbg = "${libdir}/.debug/libwvbase.so.* ${libdir}/.debug/libwvstreams.so.*"

FILES_${PN}-valgrind = "${libdir}/valgrind/wvstreams.supp"
RDEPENDS_${PN} += "perl"
