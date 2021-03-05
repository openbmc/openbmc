require binutils.inc
require binutils-${PV}.inc

DEPENDS += "flex bison zlib"

EXTRA_OECONF += "--with-sysroot=/ \
                --enable-install-libbfd \
                --enable-install-libiberty \
                --enable-shared \
                --with-system-zlib \
                "

EXTRA_OEMAKE_append_libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                "
EXTRA_OECONF_class-native = "--enable-targets=all \
                             --enable-64-bit-bfd \
                             --enable-install-libiberty \
                             --enable-install-libbfd \
                             --disable-gdb \
                             --disable-gdbserver \
                             --disable-libdecnumber \
                             --disable-readline \
                             --disable-sim \
                             --disable-werror"

PACKAGECONFIG ??= ""
PACKAGECONFIG[debuginfod] = "--with-debuginfod, --without-debuginfod, elfutils"
# gcc9.0 end up mis-compiling libbfd.so with O2 which then crashes on target
# So remove -O2 and use -Os as workaround
SELECTED_OPTIMIZATION_remove_mipsarch = "-O2"
SELECTED_OPTIMIZATION_append_mipsarch = " -Os"

do_install_class-native () {
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
PARALLEL_MAKEINST_class-target = ""
PARALLEL_MAKEINST_class-nativesdk = ""

# Split out libbfd-*.so and libopcodes-*.so so including perf doesn't include
# extra stuff
PACKAGE_BEFORE_PN += "libbfd libopcodes"
FILES_libbfd = "${libdir}/libbfd-*.so.* ${libdir}/libbfd-*.so"
FILES_libopcodes = "${libdir}/libopcodes-*.so.* ${libdir}/libopcodes-*.so"

SRC_URI_append_class-nativesdk =  " file://0003-binutils-nativesdk-Search-for-alternative-ld.so.conf.patch "

USE_ALTERNATIVES_FOR_class-nativesdk = ""
FILES_${PN}_append_class-nativesdk = " ${bindir}"

BBCLASSEXTEND = "native nativesdk"

