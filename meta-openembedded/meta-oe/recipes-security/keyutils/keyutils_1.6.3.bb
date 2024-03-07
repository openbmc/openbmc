SUMMARY = "Linux Key Management Utilities"
DESCRIPTION = "\
    Utilities to control the kernel key management facility and to provide \
    a mechanism by which the kernel call back to userspace to get a key \
    instantiated. \
    "
HOMEPAGE = "http://people.redhat.com/dhowells/keyutils"
SECTION = "base"

LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://LICENCE.GPL;md5=5f6e72824f5da505c1f4a7197f004b45 \
                    file://LICENCE.LGPL;md5=7d1cacaa3ea752b72ea5e525df54a21f"

inherit manpages ptest

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/dhowells/keyutils.git;protocol=https;branch=master \
           file://keyutils-test-fix-output-format.patch \
           file://keyutils-fix-error-report-by-adding-default-message.patch \
           file://run-ptest \
           file://fix_library_install_path.patch \
           file://0001-tests-toolbox.inc.sh-update-regex-for-getting-endian.patch \
           file://0001-Adhere-to-the-SOURCE_DATE_EPOCH-standard.patch \
           file://0001-tests-builtin_trusted-Failure-command-is-failed.patch \
           file://0002-tests-Use-head-n1-for-busybox-compatibility.patch \
           "
SRCREV = "cb3bb194cca88211cbfcdde2f10c0f43c3fb8ec3"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[manpages] = ""

EXTRA_OEMAKE = "'CFLAGS=${CFLAGS} -Wall' \
    NO_ARLIB=1 \
    BINDIR=${base_bindir} \
    SBINDIR=${base_sbindir} \
    LIBDIR=${libdir} \
    USRLIBDIR=${libdir} \
    INCLUDEDIR=${includedir} \
    ETCDIR=${sysconfdir} \
    SHAREDIR=${datadir}/keyutils \
    MANDIR=${datadir}/man \
    BUILDFOR=${SITEINFO_BITS}-bit \
    NO_GLIBC_KEYERR=1 \
    "

do_install () {
    oe_runmake DESTDIR=${D} install
}

do_install_ptest () {
    cp -r ${S}/tests ${D}${PTEST_PATH}/
    sed -i -e 's/OSDIST=Unknown/OSDIST=${DISTRO}/' ${D}${PTEST_PATH}/tests/prepare.inc.sh
}


RDEPENDS:${PN}-ptest += "bash file lsb-release make"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils"
RDEPENDS:${PN}-ptest:append:libc-musl = " musl-utils"

BBCLASSEXTEND = "native nativesdk"
