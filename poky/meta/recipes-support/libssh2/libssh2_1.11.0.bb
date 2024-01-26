SUMMARY = "A client-side C library implementing the SSH2 protocol"
HOMEPAGE = "http://www.libssh2.org/"
SECTION = "libs"

DEPENDS = "zlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=24a33237426720395ebb1dd1349ca225"

SRC_URI = "http://www.libssh2.org/download/${BP}.tar.gz \
           file://run-ptest \
           file://CVE-2023-48795.patch \
           "

SRC_URI[sha256sum] = "3736161e41e2693324deb38c26cfdc3efe6209d634ba4258db1cecff6a5ad461"

inherit autotools pkgconfig ptest

EXTRA_OECONF += "\
                 --with-libz \
                 --with-libz-prefix=${STAGING_LIBDIR} \
                 --disable-rpath \
                "
DISABLE_STATIC = ""

# only one of openssl and gcrypt could be set
PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto=openssl --with-libssl-prefix=${STAGING_LIBDIR}, , openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto=libgcrypt --with-libgcrypt-prefix=${STAGING_EXECPREFIXDIR}, , libgcrypt"

BBCLASSEXTEND = "native nativesdk"

# required for ptest on documentation
RDEPENDS:${PN}-ptest = "bash man-db openssh util-linux-col"
RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-en-us"

do_compile_ptest() {
	sed -i "/\$(MAKE) \$(AM_MAKEFLAGS) check-TESTS/d" tests/Makefile
	oe_runmake check
}

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	install -m 0755 ${S}/test-driver ${D}${PTEST_PATH}/
	cp -rf ${B}/tests/.libs/* ${D}${PTEST_PATH}/tests/
	cp -rf ${B}/tests/test_simple ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/mansyntax.sh  ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/key*  ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/openssh_server/  ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/tests/*.test  ${D}${PTEST_PATH}/tests/
	mkdir -p ${D}${PTEST_PATH}/docs
	cp -r ${S}/docs/* ${D}${PTEST_PATH}/docs/
}
