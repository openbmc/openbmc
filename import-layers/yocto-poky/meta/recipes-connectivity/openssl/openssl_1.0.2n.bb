require openssl10.inc

# For target side versions of openssl enable support for OCF Linux driver
# if they are available.

CFLAG += "-DHAVE_CRYPTODEV -DUSE_CRYPTODEV_DIGESTS"
CFLAG_append_class-native = " -fPIC"

LIC_FILES_CHKSUM = "file://LICENSE;md5=057d9218c6180e1d9ee407572b2dd225"

export DIRS = "crypto ssl apps engines"
export OE_LDFLAGS="${LDFLAGS}"

SRC_URI += "file://find.pl;subdir=openssl-${PV}/util/ \
           file://run-ptest \
           file://openssl-c_rehash.sh \
           file://configure-targets.patch \
           file://shared-libs.patch \
           file://oe-ldflags.patch \
           file://engines-install-in-libdir-ssl.patch \
           file://debian1.0.2/block_diginotar.patch \
           file://debian1.0.2/block_digicert_malaysia.patch \
           file://debian/ca.patch \
           file://debian/c_rehash-compat.patch \
           file://debian/debian-targets.patch \
           file://debian/man-dir.patch \
           file://debian/man-section.patch \
           file://debian/no-rpath.patch \
           file://debian/no-symbolic.patch \
           file://debian/pic.patch \
           file://debian1.0.2/version-script.patch \
           file://debian1.0.2/soname.patch \
           file://openssl_fix_for_x32.patch \
           file://openssl-fix-des.pod-error.patch \
           file://Makefiles-ptest.patch \
           file://ptest-deps.patch \
           file://openssl-1.0.2a-x32-asm.patch \
           file://ptest_makefile_deps.patch \
           file://configure-musl-target.patch \
           file://parallel.patch \
           file://openssl-util-perlpath.pl-cwd.patch \
           file://Use-SHA256-not-MD5-as-default-digest.patch \
           file://0001-Fix-build-with-clang-using-external-assembler.patch \
           file://0001-openssl-force-soft-link-to-avoid-rare-race.patch \
           "

SRC_URI_append_class-target = "\
           file://reproducible-cflags.patch \
           file://reproducible-mkbuildinf.patch \
           "
SRC_URI[md5sum] = "13bdc1b1d1ff39b6fd42a255e74676a4"
SRC_URI[sha256sum] = "370babb75f278c39e0c50e8c4e7493bc0f18db6867478341a832a982fd15a8fe"

PACKAGES =+ "${PN}-engines"
FILES_${PN}-engines = "${libdir}/ssl/engines/*.so ${libdir}/engines"

# The crypto_use_bigint patch means that perl's bignum module needs to be
# installed, but some distributions (for example Fedora 23) don't ship it by
# default.  As the resulting error is very misleading check for bignum before
# building.
do_configure_prepend() {
	if ! perl -Mbigint -e true; then
		bbfatal "The perl module 'bignum' was not found but this is required to build openssl.  Please install this module (often packaged as perl-bignum) and re-run bitbake."
	fi
}
