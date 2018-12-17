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
           "

SRC_URI[md5sum] = "2126fd125ea26b73b20f01fcd5940369"
SRC_URI[sha256sum] = "53e15a2b5c1bc80161d42e9f69792a3fa18332b7b771910131004eb520004a28"

S = "${WORKDIR}/imap-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = ",,libpam"

EXTRA_OEMAKE = "CC='${CC}' ARRC='${AR} -rc' RANLIB='${RANLIB}'"

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

RPROVIDES_${PN} = "libc-client"
RREPLACES_${PN} = "libc-client"
RCONFLICTS_${PN} = "libc-client"

ALLOW_EMPTY_${PN} = "1"

