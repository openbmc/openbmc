SUMMARY = "Utilities and libraries for handling compiled object files"
HOMEPAGE = "https://sourceware.org/elfutils"
SECTION = "base"
LICENSE = "GPLv2 & LGPLv3+ & GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "libtool bzip2 zlib virtual/libintl"
DEPENDS_append_libc-musl = " argp-standalone fts "
# The Debian patches below are from:
# http://ftp.de.debian.org/debian/pool/main/e/elfutils/elfutils_0.175-1.debian.tar.xz
SRC_URI = "https://sourceware.org/elfutils/ftp/${PV}/${BP}.tar.bz2 \
           file://0001-dso-link-change.patch \
           file://0002-Fix-elf_cvt_gunhash-if-dest-and-src-are-same.patch \
           file://0003-fixheadercheck.patch \
           file://0004-Disable-the-test-to-convert-euc-jp.patch \
           file://0006-Fix-build-on-aarch64-musl.patch \
           file://0007-Fix-control-path-where-we-have-str-as-uninitialized-.patch \
           file://0001-libasm-may-link-with-libbz2-if-found.patch \
           file://0001-libelf-elf_end.c-check-data_list.data.d.d_buf-before.patch \
           file://debian/hppa_backend.diff \
           file://debian/arm_backend.diff \
           file://debian/mips_backend.diff \
           file://debian/mips_readelf_w.patch \
           file://debian/kfreebsd_path.patch \
           file://debian/0001-Ignore-differences-between-mips-machine-identifiers.patch \
           file://debian/0002-Add-support-for-mips64-abis-in-mips_retval.c.patch \
           file://debian/0003-Add-mips-n64-relocation-format-hack.patch \
           file://debian/hurd_path.patch \
           file://debian/ignore_strmerge.diff \
           file://debian/disable_werror.patch \
           file://debian/testsuite-ignore-elflint.diff \
           file://0001-skip-the-test-when-gcc-not-deployed.patch \
           file://run-ptest \
           file://ptest.patch \
           file://musl.patch \
           "
SRC_URI_append_libc-musl = " file://0008-build-Provide-alternatives-for-glibc-assumptions-hel.patch \
                             file://0001-fix-err-variable-and-function-conflicts.patch \
"

SRC_URI[md5sum] = "077e4f49320cad82bf17a997068b1db9"
SRC_URI[sha256sum] = "eb5747c371b0af0f71e86215a5ebb88728533c3a104a43d4231963f308cd1023"

inherit autotools gettext ptest

EXTRA_OECONF = "--program-prefix=eu- --without-lzma"
EXTRA_OECONF_append_class-native = " --without-bzlib"
RDEPENDS_${PN}-ptest = "libasm libelf bash make coreutils ${PN}-binutils ${PN}"

EXTRA_OECONF_append_class-target += "--disable-tests-rpath"

do_install_append() {
	if [ "${TARGET_ARCH}" != "x86_64" ] && [ -z `echo "${TARGET_ARCH}"|grep 'i.86'` ];then
		rm -f ${D}${bindir}/eu-objdump
	fi
}

do_compile_ptest() {
	cd ${B}/tests
	oe_runmake buildtest-TESTS oecheck
}

do_install_ptest() {
	if [ ${PTEST_ENABLED} = "1" ]; then
		cp -r ${S}/tests/                       ${D}${PTEST_PATH}
		cp -r ${B}/tests/*                      ${D}${PTEST_PATH}/tests
		cp -r ${B}/config.h                     ${D}${PTEST_PATH}
		cp -r ${B}/backends                     ${D}${PTEST_PATH}
		sed -i '/^Makefile:/c Makefile:'        ${D}${PTEST_PATH}/tests/Makefile
		find ${D}${PTEST_PATH} -type f -name *.[hoc] | xargs -i rm {}
	fi
}

EXTRA_OEMAKE_class-native = ""
EXTRA_OEMAKE_class-nativesdk = ""

ALLOW_EMPTY_${PN}_libc-musl = "1"

BBCLASSEXTEND = "native nativesdk"

# Package utilities separately
PACKAGES =+ "${PN}-binutils libelf libasm libdw"

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
# Some packages have the version preceeding the .so instead properly
# versioned .so.<version>, so we need to reorder and repackage.
#FILES_${PN} += "${libdir}/*-${PV}.so ${base_libdir}/*-${PV}.so"
#FILES_SOLIBSDEV = "${libdir}/libasm.so ${libdir}/libdw.so ${libdir}/libelf.so"

# The package contains symlinks that trip up insane
INSANE_SKIP_${MLPREFIX}libdw = "dev-so"
