SUMMARY = "Linux Key Management Utilities"
DESCRIPTION = "\
    Utilities to control the kernel key management facility and to provide \
    a mechanism by which the kernel call back to userspace to get a key \
    instantiated. \
    "
HOMEPAGE = "http://people.redhat.com/dhowells/keyutils"
SECTION = "base"

LICENSE = "LGPLv2.1+ & GPLv2.0+"

LIC_FILES_CHKSUM = "file://LICENCE.GPL;md5=5f6e72824f5da505c1f4a7197f004b45 \
                    file://LICENCE.LGPL;md5=7d1cacaa3ea752b72ea5e525df54a21f"

inherit siteinfo autotools-brokensep ptest

SRC_URI = "http://people.redhat.com/dhowells/keyutils/${BP}.tar.bz2 \
           file://keyutils-test-fix-output-format.patch \
           file://keyutils-fix-error-report-by-adding-default-message.patch \
           file://run-ptest \
           file://fix_library_install_path.patch \
           "

SRC_URI[md5sum] = "919af7f33576816b423d537f8a8692e8"
SRC_URI[sha256sum] = "c8b15722ae51d95b9ad76cc6d49a4c2cc19b0c60f72f61fb9bf43eea7cbd64ce"

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
    install -d ${D}/${libdir}/pkgconfig
    oe_runmake DESTDIR=${D} install
}

do_install_ptest () {
    cp -r ${S}/tests ${D}${PTEST_PATH}/
    sed -i -e 's/OSDIST=Unknown/OSDIST=${DISTRO}/' ${D}${PTEST_PATH}/tests/prepare.inc.sh
}


RDEPENDS_${PN}-ptest += "lsb-release"
RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-utils"
RDEPENDS_${PN}-ptest_append_libc-musl = " musl-utils"

BBCLASSEXTEND = "native nativesdk"
