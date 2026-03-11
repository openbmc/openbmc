SUMMARY = "Utilities for managing POSIX Access Control Lists"
DESCRIPTION = "ACL allows you to provide different levels of access to files \
and folders for different users."

HOMEPAGE = "http://savannah.nongnu.org/projects/acl/"
BUGTRACKER = "http://savannah.nongnu.org/bugs/?group=acl"

SECTION = "libs"

LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:lib${BPN} = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
                    file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764"

DEPENDS = "attr"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/acl/${BP}.tar.gz \
           file://0001-libmisc-__acl_get_uid-fix-memory-wasting-loop-if-use.patch \
           file://0001-test-misc.test-Don-t-mix-stdout-and-stderr.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "5f2bdbad629707aa7d85c623f994aa8a1d2dec55a73de5205bac0bf6058a2f7c"

inherit autotools gettext ptest

EXTRA_OECONF += "--enable-largefile"

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*${SOLIBS}"

do_compile_ptest() {
        oe_runmake libtestlookup.la libtestlookup_la_CFLAGS=-DBASEDIR=\\\"${PTEST_PATH}\\\"
}

do_install_ptest() {
        install -m755 ${S}/test/run ${S}/test/sort-getfacl-output ${D}${PTEST_PATH}/
        install -m644 ${S}/test/*.acl ${D}${PTEST_PATH}/

        # Install the tests
        for t in $(makefile-getvar ${S}/test/Makemodule.am TESTS); do
                install -m644 ${S}/$t ${D}${PTEST_PATH}/
        done
        # Remove the tests that are expected to fail (they need a NFS server configured)
        for t in $(makefile-getvar ${S}/test/Makemodule.am XFAIL_TESTS); do
                rm ${D}${PTEST_PATH}/$(basename $t)
        done
        # These tests need a very specific user/group setup
        # (https://savannah.nongnu.org/bugs/index.php?66927)
        rm -f ${D}${PTEST_PATH}/getfacl.test ${D}${PTEST_PATH}/permissions.test ${D}${PTEST_PATH}/restore.test ${D}${PTEST_PATH}/setfacl.test

        ${B}/libtool --mode=install install ${B}/libtestlookup.la ${D}${PTEST_PATH}/
        rm -f ${D}${PTEST_PATH}/*.la
        install -d ${D}${PTEST_PATH}/test
        install -m644 ${S}/test/test.passwd ${S}/test/test.group ${D}${PTEST_PATH}/test
}

RDEPENDS:${PN}-ptest = "acl \
                        coreutils \
                        perl \
                        perl-module-cwd \
                        perl-module-file-basename \
                        perl-module-file-path \
                        perl-module-filehandle \
                        perl-module-getopt-std \
                        perl-module-posix \
                       "

BBCLASSEXTEND = "native nativesdk"
