SUMMARY = "Utilities and libraries for handling compiled object files"
HOMEPAGE = "https://sourceware.org/elfutils"
SECTION = "base"
LICENSE = "(GPLv3 & Elfutils-Exception)"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "libtool bzip2 zlib virtual/libintl"
DEPENDS_append_libc-musl = " argp-standalone fts "
# The Debian patches below are from:
# http://ftp.de.debian.org/debian/pool/main/e/elfutils/elfutils_0.170-0.5.debian.tar.xz
SRC_URI = "https://sourceware.org/elfutils/ftp/${PV}/${BP}.tar.bz2 \
           file://0001-dso-link-change.patch \
           file://0002-Fix-elf_cvt_gunhash-if-dest-and-src-are-same.patch \
           file://0003-fixheadercheck.patch \
           file://0004-Disable-the-test-to-convert-euc-jp.patch \
           file://0005-fix-a-stack-usage-warning.patch \
           file://0006-Fix-build-on-aarch64-musl.patch \
           file://0007-Fix-control-path-where-we-have-str-as-uninitialized-.patch \
           file://0001-libasm-may-link-with-libbz2-if-found.patch \
           file://0001-libelf-elf_end.c-check-data_list.data.d.d_buf-before.patch \
           file://0001-libdwfl-Sanity-check-partial-core-file-data-reads.patch \
           file://0001-size-Handle-recursive-ELF-ar-files.patch \
           file://0001-arlib-Check-that-sh_entsize-isn-t-zero.patch \
           file://debian/0001-hppa_backend.patch \
           file://debian/0001-arm_backend.patch \
           file://debian/0001-mips_backend.patch \
           file://debian/0001-testsuite-ignore-elflint.patch \
           file://debian/0001-mips_readelf_w.patch \
           file://debian/0001-Ignore-differences-between-mips-machine-identifiers.patch \
           file://debian/0002-Add-support-for-mips64-abis-in-mips_retval.c.patch \
           file://debian/0003-Add-mips-n64-relocation-format-hack.patch \
           file://debian/ignore_strmerge.diff \
           file://debian/0001-fix-gcc7-ftbfs.patch \
           file://debian/0001-disable_werror.patch \
           "
SRC_URI_append_libc-musl = " file://0008-build-Provide-alternatives-for-glibc-assumptions-hel.patch"

SRC_URI[md5sum] = "48bec24c0c8b2c16820326956dff9378"
SRC_URI[sha256sum] = "cdf27e70076e10a29539d89e367101d516bc4aa11b0d7777fe52139e3fcad08a"

inherit autotools gettext

EXTRA_OECONF = "--program-prefix=eu- --without-lzma"
EXTRA_OECONF_append_class-native = " --without-bzlib"

do_install_append() {
	if [ "${TARGET_ARCH}" != "x86_64" ] && [ -z `echo "${TARGET_ARCH}"|grep 'i.86'` ];then
		rm -f ${D}${bindir}/eu-objdump
	fi
}

EXTRA_OEMAKE_class-native = ""
EXTRA_OEMAKE_class-nativesdk = ""

ALLOW_EMPTY_${PN}_libc-musl = "1"

BBCLASSEXTEND = "native nativesdk"

# Package utilities separately
PACKAGES =+ "${PN}-binutils libelf libasm libdw"
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
