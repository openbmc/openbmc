SUMMARY = "Utilities and libraries for handling compiled object files"
HOMEPAGE = "https://sourceware.org/elfutils"
DESCRIPTION = "elfutils is a collection of utilities and libraries to read, create and modify ELF binary files, find and handle DWARF debug data, symbols, thread state and stacktraces for processes and core files on GNU/Linux."
SECTION = "base"
LICENSE = "GPLv2 & GPLv2+ & LGPLv3+ & GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://debuginfod/debuginfod-client.c;endline=27;md5=f8e9d171c401c493ec45a0b2992ea2ed \
                    "
DEPENDS = "zlib virtual/libintl"
DEPENDS_append_libc-musl = " argp-standalone fts musl-obstack "
# The Debian patches below are from:
# http://ftp.de.debian.org/debian/pool/main/e/elfutils/elfutils_0.176-1.debian.tar.xz
SRC_URI = "https://sourceware.org/elfutils/ftp/${PV}/${BP}.tar.bz2 \
           file://0001-dso-link-change.patch \
           file://0002-Fix-elf_cvt_gunhash-if-dest-and-src-are-same.patch \
           file://0003-fixheadercheck.patch \
           file://0006-Fix-build-on-aarch64-musl.patch \
           file://0001-libasm-may-link-with-libbz2-if-found.patch \
           file://0001-libelf-elf_end.c-check-data_list.data.d.d_buf-before.patch \
           file://0001-skip-the-test-when-gcc-not-deployed.patch \
           file://run-ptest \
           file://ptest.patch \
           file://0001-tests-Makefile.am-compile-test_nlist-with-standard-C.patch \
           file://0001-add-support-for-ipkg-to-debuginfod.cxx.patch \
           file://0001-debuginfod-debuginfod-client.c-correct-string-format.patch \
           "
SRC_URI_append_libc-musl = " \
           file://0002-musl-libs.patch \
           file://0003-musl-utils.patch \
           file://0004-Fix-error-on-musl.patch \
           file://0015-config-eu.am-do-not-use-Werror.patch \
           "
SRC_URI[sha256sum] = "c3637c208d309d58714a51e61e63f1958808fead882e9b607506a29e5474f2c5"

inherit autotools gettext ptest pkgconfig

EXTRA_OECONF = "--program-prefix=eu-"

DEPENDS_BZIP2 = "bzip2-replacement-native"
DEPENDS_BZIP2_class-target = "bzip2"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'debuginfod', 'debuginfod libdebuginfod', '', d)}"
PACKAGECONFIG[bzip2] = "--with-bzlib,--without-bzlib,${DEPENDS_BZIP2}"
PACKAGECONFIG[xz] = "--with-lzma,--without-lzma,xz"
PACKAGECONFIG[libdebuginfod] = "--enable-libdebuginfod,--disable-libdebuginfod,curl"
PACKAGECONFIG[debuginfod] = "--enable-debuginfod,--disable-debuginfod,libarchive sqlite3 libmicrohttpd"

RDEPENDS_${PN}-ptest += "libasm libelf bash make coreutils ${PN}-binutils iproute2-ss bsdtar"

EXTRA_OECONF_append_class-target = " --disable-tests-rpath"

RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-utils"

do_compile_ptest() {
	cd ${B}/tests
	oe_runmake buildtest-TESTS oecheck
}

do_install_ptest() {
	if [ ${PTEST_ENABLED} = "1" ]; then
		# copy the files which needed by the cases
		TEST_FILES="strip strip.o addr2line elfcmp objdump readelf size.o nm.o nm elflint elfcompress elfclassify stack unstrip"
		install -d -m 755                       ${D}${PTEST_PATH}/src
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

EXTRA_OEMAKE_class-native = ""
EXTRA_OEMAKE_class-nativesdk = ""

BBCLASSEXTEND = "native nativesdk"

# Package utilities separately
PACKAGES =+ "${PN}-binutils libelf libasm libdw libdebuginfod"

# shared libraries are licensed GPLv2 or GPLv3+, binaries GPLv3+
# according to NEWS file:
# "The license is now GPLv2/LGPLv3+ for the libraries and GPLv3+ for stand-alone
# programs. There is now also a formal CONTRIBUTING document describing how to
# submit patches."
LICENSE_${PN}-binutils = "GPLv3+"
LICENSE_${PN} = "GPLv3+"
LICENSE_libelf = "GPLv2 | LGPLv3+"
LICENSE_libasm = "GPLv2 | LGPLv3+"
LICENSE_libdw = "GPLv2 | LGPLv3+"
LICENSE_libdebuginfod = "GPLv2+ | LGPLv3+"

FILES_${PN}-binutils = "\
    ${bindir}/eu-addr2line \
    ${bindir}/eu-ld \
    ${bindir}/eu-nm \
    ${bindir}/eu-readelf \
    ${bindir}/eu-size \
    ${bindir}/eu-strip"

FILES_libelf = "${libdir}/libelf-${PV}.so ${libdir}/libelf.so.*"
FILES_libasm = "${libdir}/libasm-${PV}.so ${libdir}/libasm.so.*"
FILES_libdw  = "${libdir}/libdw-${PV}.so ${libdir}/libdw.so.* ${libdir}/elfutils/lib*"
FILES_libdebuginfod = "${libdir}/libdebuginfod-${PV}.so ${libdir}/libdebuginfod.so.*"
# Some packages have the version preceeding the .so instead properly
# versioned .so.<version>, so we need to reorder and repackage.
#FILES_${PN} += "${libdir}/*-${PV}.so ${base_libdir}/*-${PV}.so"
#FILES_SOLIBSDEV = "${libdir}/libasm.so ${libdir}/libdw.so ${libdir}/libelf.so"

# The package contains symlinks that trip up insane
INSANE_SKIP_${MLPREFIX}libdw = "dev-so"
# The nlist binary in the tests uses explicitly minimal compiler flags
INSANE_SKIP_${PN}-ptest += "ldflags"

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
    ${PKGD}${PTEST_PATH}/libelf/libelf.so \
    ${PKGD}${PTEST_PATH}/libdw/libdw.so \
    ${PKGD}${PTEST_PATH}/libasm/libasm.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_i386.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_x86_64.so \
"

PRIVATE_LIBS_${PN}-ptest = "libdw.so.1 libelf.so.1 libasm.so.1 libdebuginfod.so.1"
