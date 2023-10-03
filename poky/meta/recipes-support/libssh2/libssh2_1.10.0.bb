SUMMARY = "A client-side C library implementing the SSH2 protocol"
HOMEPAGE = "http://www.libssh2.org/"
SECTION = "libs"

DEPENDS = "zlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3e089ad0cf27edf1e7f261dfcd06acc7"

SRC_URI = "http://www.libssh2.org/download/${BP}.tar.gz \
           file://fix-ssh2-test.patch \
           file://run-ptest \
           file://CVE-2020-22218.patch \
           "

SRC_URI[sha256sum] = "2d64e90f3ded394b91d3a2e774ca203a4179f69aebee03003e5a6fa621e41d51"

inherit autotools pkgconfig ptest

EXTRA_OECONF += "\
                 --with-libz \
                 --with-libz-prefix=${STAGING_LIBDIR} \
                "

# only one of openssl and gcrypt could be set
PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto=openssl --with-libssl-prefix=${STAGING_LIBDIR}, , openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto=libgcrypt --with-libgcrypt-prefix=${STAGING_EXECPREFIXDIR}, , libgcrypt"

BBCLASSEXTEND = "native nativesdk"

# required for ptest on documentation
RDEPENDS:${PN}-ptest = "man-db openssh util-linux-col"
RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-en-us"

do_compile_ptest() {
	sed -i "/\$(MAKE) \$(AM_MAKEFLAGS) check-TESTS/d" tests/Makefile
	oe_runmake check
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	install -m 0755 ${S}/test-driver ${D}${PTEST_PATH}/
	cp -rf ${B}/tests/.libs/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/mansyntax.sh  ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/ssh2.sh  ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/etc ${D}${PTEST_PATH}/tests/
	mkdir -p ${D}${PTEST_PATH}/docs
	cp -r ${S}/docs/* ${D}${PTEST_PATH}/docs/
}
