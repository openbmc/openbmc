require binutils.inc
require binutils-${PV}.inc

DEPENDS += "zlib"

EXTRA_OECONF += "--with-sysroot=/ \
                --enable-install-libbfd \
                --enable-install-libiberty \
                --enable-shared \
                --with-system-zlib \
                "

EXTRA_OEMAKE:append:libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                "
EXTRA_OECONF:class-native = "--enable-targets=all \
                             --enable-64-bit-bfd \
                             --enable-install-libiberty \
                             --enable-install-libbfd \
                             --disable-gdb \
                             --disable-gdbserver \
                             --disable-libdecnumber \
                             --disable-readline \
                             --disable-sim \
                             --disable-werror"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'debuginfod', d)}"
PACKAGECONFIG[debuginfod] = "--with-debuginfod, --without-debuginfod, elfutils"

do_install:class-native () {
	autotools_do_install

	# Install the libiberty header
	install -d ${D}${includedir}
	install -m 644 ${S}/include/ansidecl.h ${D}${includedir}
	install -m 644 ${S}/include/libiberty.h ${D}${includedir}

	# We only want libiberty, libbfd and libopcodes
	rm -rf ${D}${bindir}
	rm -rf ${D}${prefix}/${TARGET_SYS}
	rm -rf ${D}${prefix}/lib/ldscripts
	rm -rf ${D}${prefix}/share/info
	rm -rf ${D}${prefix}/share/locale
	rm -rf ${D}${prefix}/share/man
	rmdir ${D}${prefix}/share || :
	rmdir ${D}/${libdir}/gcc-lib || :
	rmdir ${D}/${libdir}64/gcc-lib || :
	rmdir ${D}/${libdir} || :
	rmdir ${D}/${libdir}64 || :
}

# libctf races with libbfd
PARALLEL_MAKEINST:class-target = ""
PARALLEL_MAKEINST:class-nativesdk = ""

# Split out libbfd-*.so and libopcodes-*.so so including perf doesn't include
# extra stuff
PACKAGE_BEFORE_PN += "libbfd libopcodes"
FILES:libbfd = "${libdir}/libbfd-*.so.* ${libdir}/libbfd-*.so"
FILES:libopcodes = "${libdir}/libopcodes-*.so.* ${libdir}/libopcodes-*.so"

SRC_URI:append:class-nativesdk =  " file://0003-binutils-nativesdk-Search-for-alternative-ld.so.conf.patch "

USE_ALTERNATIVES_FOR:class-nativesdk = ""
FILES:${PN}:append:class-nativesdk = " ${bindir}"

BBCLASSEXTEND = "native nativesdk"

