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


inherit siteinfo ptest

SRC_URI = "http://people.redhat.com/dhowells/keyutils/${BP}.tar.bz2 \
           file://keyutils-use-relative-path-for-link.patch \
           file://keyutils-test-fix-output-format.patch \
           file://keyutils-fix-error-report-by-adding-default-message.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "3771676319bc7b84b1549b5c63ff5243"
SRC_URI[sha256sum] = "115c3deae7f181778fd0e0ffaa2dad1bf1fe2f5677cf2e0e348cdb7a1c93afb6"

EXTRA_OEMAKE = "'CFLAGS=${CFLAGS} -Wall' \
    NO_ARLIB=1 \
    BINDIR=${base_bindir} \
    SBINDIR=${base_sbindir} \
    LIBDIR=${base_libdir} \
    USRLIBDIR=${base_libdir} \
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

RDEPENDS_${PN}-ptest += "glibc-utils"
