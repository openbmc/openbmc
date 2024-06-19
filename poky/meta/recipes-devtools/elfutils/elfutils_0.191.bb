SUMMARY = "Utilities and libraries for handling compiled object files"
HOMEPAGE = "https://sourceware.org/elfutils"
DESCRIPTION = "elfutils is a collection of utilities and libraries to read, create and modify ELF binary files, find and handle DWARF debug data, symbols, thread state and stacktraces for processes and core files on GNU/Linux."
SECTION = "base"
LICENSE = "( GPL-2.0-or-later | LGPL-3.0-or-later ) & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://debuginfod/debuginfod-client.c;endline=28;md5=f0a7c3170776866ee94e8f9225a6ad79 \
                    "
DEPENDS = "zlib virtual/libintl"
DEPENDS:append:libc-musl = " argp-standalone fts musl-legacy-error musl-obstack"
# The Debian patches below are from:
# http://ftp.de.debian.org/debian/pool/main/e/elfutils/elfutils_0.176-1.debian.tar.xz
SRC_URI = "https://sourceware.org/elfutils/ftp/${PV}/${BP}.tar.bz2 \
           file://run-ptest \
           file://0001-dso-link-change.patch \
           file://0002-Fix-elf_cvt_gunhash-if-dest-and-src-are-same.patch \
           file://0003-fixheadercheck.patch \
           file://0001-libasm-may-link-with-libbz2-if-found.patch \
           file://0001-libelf-elf_end.c-check-data_list.data.d.d_buf-before.patch \
           file://0001-skip-the-test-when-gcc-not-deployed.patch \
           file://ptest.patch \
           file://0001-tests-Makefile.am-compile-test_nlist-with-standard-C.patch \
           file://0001-debuginfod-Remove-unused-variable.patch \
           file://0001-srcfiles-fix-unused-variable-BUFFER_SIZE.patch \
           "
SRC_URI:append:libc-musl = " \
           file://0003-musl-utils.patch \
           "
SRC_URI[sha256sum] = "df76db71366d1d708365fc7a6c60ca48398f14367eb2b8954efc8897147ad871"

inherit autotools gettext ptest pkgconfig

EXTRA_OECONF = "--program-prefix=eu-"

# Only used at runtime for make check but we want deterministic makefiles for ptest so hardcode
CACHED_CONFIGUREVARS += "ac_cv_prog_HAVE_BUNZIP2=yes"

BUILD_CFLAGS += "-Wno-error=stringop-overflow"

DEPENDS_BZIP2 = "bzip2-replacement-native"
DEPENDS_BZIP2:class-target = "bzip2"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'debuginfod', 'debuginfod libdebuginfod', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'minidebuginfo', 'xz', '', d)} \
                  "
PACKAGECONFIG[bzip2] = "--with-bzlib,--without-bzlib,${DEPENDS_BZIP2}"
PACKAGECONFIG[xz] = "--with-lzma,--without-lzma,xz"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"
PACKAGECONFIG[libdebuginfod] = "--enable-libdebuginfod,--disable-libdebuginfod,curl"
PACKAGECONFIG[debuginfod] = "--enable-debuginfod,--disable-debuginfod,libarchive sqlite3 libmicrohttpd"

RDEPENDS:${PN}-ptest += "libasm libelf bash make coreutils ${PN}-binutils iproute2-ss bsdtar gcc-symlinks binutils-symlinks libgcc-dev"

EXTRA_OECONF:append:class-target = " --disable-tests-rpath"

# symver functions not currently supported on microblaze
EXTRA_OECONF:append:class-target:microblaze = " --disable-symbol-versioning"

RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils glibc-dbg glibc-dev"
INSANE_SKIP:${PN}-ptest = "debug-deps dev-deps"

do_compile_ptest() {
	cd ${B}/tests
	oe_runmake buildtest-TESTS oecheck
}
PTEST_PARALLEL_MAKE = ""

do_install_ptest() {
	if [ ${PTEST_ENABLED} = "1" ]; then
		# copy the files which needed by the cases
		TEST_FILES="strip strip.o addr2line elfcmp objdump readelf size.o nm.o nm elflint elfcompress elfclassify stack unstrip srcfiles"
		install -d -m 755                       ${D}${PTEST_PATH}/src
		install -d -m 755                       ${D}${PTEST_PATH}/lib
		install -d -m 755                       ${D}${PTEST_PATH}/libelf
		install -d -m 755                       ${D}${PTEST_PATH}/libdw
		install -d -m 755                       ${D}${PTEST_PATH}/libdwfl
		install -d -m 755                       ${D}${PTEST_PATH}/libdwelf
		install -d -m 755                       ${D}${PTEST_PATH}/libasm
		install -d -m 755                       ${D}${PTEST_PATH}/libcpu
		install -d -m 755                       ${D}${PTEST_PATH}/libebl
		for test_file in ${TEST_FILES}; do
			if [ -f ${B}/src/${test_file} ]; then
				cp -r ${B}/src/${test_file} ${D}${PTEST_PATH}/src
			fi
		done
		cp ${D}${libdir}/libelf-${PV}.so ${D}${PTEST_PATH}/libelf/libelf.so
		cp ${D}${libdir}/libdw-${PV}.so ${D}${PTEST_PATH}/libdw/libdw.so
		cp ${D}${libdir}/libasm-${PV}.so ${D}${PTEST_PATH}/libasm/libasm.so
		cp ${B}/libcpu/libcpu.a ${D}${PTEST_PATH}/libcpu/
		cp ${B}/libebl/libebl.a ${D}${PTEST_PATH}/libebl/
		cp ${B}/lib/libeu.a ${D}${PTEST_PATH}/lib/
		cp ${S}/libelf/*.h             ${D}${PTEST_PATH}/libelf/
		cp ${S}/libdw/*.h              ${D}${PTEST_PATH}/libdw/
		cp ${S}/libdwfl/*.h            ${D}${PTEST_PATH}/libdwfl/
		cp ${S}/libdwelf/*.h           ${D}${PTEST_PATH}/libdwelf/
		cp ${S}/libasm/*.h             ${D}${PTEST_PATH}/libasm/
		cp -r ${S}/tests/                       ${D}${PTEST_PATH}
		cp -r ${B}/tests/*                      ${D}${PTEST_PATH}/tests
		cp -r ${B}/config.h                     ${D}${PTEST_PATH}
		cp -r ${B}/backends                     ${D}${PTEST_PATH}
		cp -r ${B}/debuginfod                   ${D}${PTEST_PATH}
		sed -i '/^Makefile:/c Makefile:'        ${D}${PTEST_PATH}/tests/Makefile
		find ${D}${PTEST_PATH} -type f -name *.[hoc] | xargs -i rm {}
	fi
}

EXTRA_OEMAKE:class-native = ""
EXTRA_OEMAKE:class-nativesdk = ""

BBCLASSEXTEND = "native nativesdk"

# Package utilities and libraries are listed separately
PACKAGES =+ "${PN}-binutils libelf libasm libdw libdebuginfod"

# According to the upstream website https://sourceware.org/elfutils, the latest
# license policy is as follows:
# "License. The libraries and backends are dual GPLv2+/LGPLv3+. The utilities
# are GPLv3+."
LICENSE:${PN}-binutils = "GPL-3.0-or-later"
LICENSE:${PN} = "GPL-3.0-or-later"
LICENSE:libelf = "GPL-2.0-or-later | LGPL-3.0-or-later"
LICENSE:libasm = "GPL-2.0-or-later | LGPL-3.0-or-later"
LICENSE:libdw = "GPL-2.0-or-later | LGPL-3.0-or-later"
LICENSE:libdebuginfod = "GPL-2.0-or-later | LGPL-3.0-or-later"

FILES:${PN}-binutils = "\
    ${bindir}/eu-addr2line \
    ${bindir}/eu-ld \
    ${bindir}/eu-nm \
    ${bindir}/eu-readelf \
    ${bindir}/eu-size \
    ${bindir}/eu-strip"

FILES:libelf = "${libdir}/libelf-${PV}.so ${libdir}/libelf.so.*"
FILES:libasm = "${libdir}/libasm-${PV}.so ${libdir}/libasm.so.*"
FILES:libdw  = "${libdir}/libdw-${PV}.so ${libdir}/libdw.so.* ${libdir}/elfutils/lib*"
FILES:libdebuginfod = "${libdir}/libdebuginfod-${PV}.so ${libdir}/libdebuginfod.so.*"
# Some packages have the version preceeding the .so instead properly
# versioned .so.<version>, so we need to reorder and repackage.
#FILES:${PN} += "${libdir}/*-${PV}.so ${base_libdir}/*-${PV}.so"
#FILES_SOLIBSDEV = "${libdir}/libasm.so ${libdir}/libdw.so ${libdir}/libelf.so"

# The package contains symlinks that trip up insane
INSANE_SKIP:${MLPREFIX}libdw = "dev-so"
# The nlist binary in the tests uses explicitly minimal compiler flags
INSANE_SKIP:${PN}-ptest += "ldflags"

# avoid stripping some generated binaries otherwise some of the tests such as test-nlist,
# run-strip-reloc.sh, run-strip-strmerge.sh and so on will fail
INHIBIT_PACKAGE_STRIP_FILES = "\
    ${PKGD}${PTEST_PATH}/tests/test-nlist \
    ${PKGD}${PTEST_PATH}/tests/elfstrmerge \
    ${PKGD}${PTEST_PATH}/tests/backtrace-child \
    ${PKGD}${PTEST_PATH}/tests/backtrace-data \
    ${PKGD}${PTEST_PATH}/tests/backtrace-dwarf \
    ${PKGD}${PTEST_PATH}/tests/deleted \
    ${PKGD}${PTEST_PATH}/tests/dwfllines \
    ${PKGD}${PTEST_PATH}/src/strip \
    ${PKGD}${PTEST_PATH}/src/addr2line \
    ${PKGD}${PTEST_PATH}/src/elfcmp \
    ${PKGD}${PTEST_PATH}/src/objdump \
    ${PKGD}${PTEST_PATH}/src/readelf \
    ${PKGD}${PTEST_PATH}/src/nm \
    ${PKGD}${PTEST_PATH}/src/elflint \
    ${PKGD}${PTEST_PATH}/src/elfclassify \
    ${PKGD}${PTEST_PATH}/src/stack \
    ${PKGD}${PTEST_PATH}/src/unstrip \
    ${PKGD}${PTEST_PATH}/src/srcfiles \
    ${PKGD}${PTEST_PATH}/libelf/libelf.so \
    ${PKGD}${PTEST_PATH}/libdw/libdw.so \
    ${PKGD}${PTEST_PATH}/libasm/libasm.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_i386.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_x86_64.so \
"

PRIVATE_LIBS:${PN}-ptest = "libdw.so.1 libelf.so.1 libasm.so.1 libdebuginfod.so.1"
