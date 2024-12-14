SUMMARY = "UW c-client library for mail protocols"
SECTION = "devel"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a6a4ddbb7cd2999f6827ee143f6fcd97"

DEPENDS = "openssl virtual/crypt"

SRC_URI = "https://fossies.org/linux/misc/old/imap-${PV}.tar.gz \
           file://quote_cctype.patch \
           file://imap-2007e-shared.patch \
           file://imap-2007f-format-security.patch \
           file://0001-Support-OpenSSL-1.1.patch \
           file://0001-Define-prototype-for-safe_flock.patch \
           file://0001-Do-not-build-mtest.patch \
           file://0002-tmail-Include-ctype.h-for-isdigit.patch \
           file://0001-Fix-Wincompatible-function-pointer-types.patch \
           file://uw-imap-newer-tls.patch \
           file://uw-imap-fix-incompatible-pointer-types.patch \
           "

SRC_URI[sha256sum] = "53e15a2b5c1bc80161d42e9f69792a3fa18332b7b771910131004eb520004a28"

S = "${WORKDIR}/imap-${PV}"

CVE_STATUS[CVE-2005-0198] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = ",,libpam"

EXTRA_OEMAKE = "CC='${CC} -std=c99 -D_GNU_SOURCE' ARRC='${AR} -rc' RANLIB='${RANLIB}' EXTRACFLAGS='${CFLAGS}'"

HEADERS = "src/c-client/*.h src/osdep/unix/*.h c-client/auths.c c-client/linkage.c c-client/linkage.h c-client/osdep.h"

do_compile() {
    echo "SSLINCLUDE=${STAGING_INCDIR} SSLLIB=${STAGING_LIBDIR}" > ${S}/SPECIALS
    oe_runmake ${@bb.utils.contains('PACKAGECONFIG', 'pam', 'lnp', 'slx', d)}
}

do_install() {
    install -d ${D}${includedir}/c-client
    install ${HEADERS} ${D}${includedir}/c-client
    install -d ${D}${libdir}
    install c-client/c-client.a ${D}${libdir}/libc-client.a
}

RPROVIDES:${PN} = "libc-client"
RREPLACES:${PN} = "libc-client"
RCONFLICTS:${PN} = "libc-client"

ALLOW_EMPTY:${PN} = "1"

PARALLEL_MAKE = ""

# http://errors.yoctoproject.org/Errors/Details/766915/
# unix.c:235:21: error: passing argument 2 of 'utime' from incompatible pointer type [-Wincompatible-pointer-types]
# unix.c:1002:15: error: passing argument 2 of 'utime' from incompatible pointer type [-Wincompatible-pointer-types]
# unix.c:1163:15: error: passing argument 2 of 'utime' from incompatible pointer type [-Wincompatible-pointer-types]
# unix.c:1428:40: error: passing argument 2 of 'utime' from incompatible pointer type [-Wincompatible-pointer-types]
# unix.c:2254:33: error: passing argument 2 of 'utime' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
