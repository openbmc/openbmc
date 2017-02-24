DESCRIPTION = "The Apache HTTP Server is a powerful, efficient, and \
extensible web server."
SUMMARY = "Apache HTTP Server"
HOMEPAGE = "http://httpd.apache.org/"
DEPENDS = "expat-native pcre-native apr-native apr-util-native"
SECTION = "net"
LICENSE = "Apache-2.0"

inherit autotools pkgconfig native

SRC_URI = "http://www.apache.org/dist/httpd/httpd-${PV}.tar.bz2 \
           file://0001-configure-use-pkg-config-for-PCRE-detection.patch \
          "

S = "${WORKDIR}/httpd-${PV}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=dbff5a2b542fa58854455bf1a0b94b83"
SRC_URI[md5sum] = "04f19c60e810c028f5240a062668a688"
SRC_URI[sha256sum] = "0c1694b2aad7765896faf92843452ee2555b9591ae10d4f19b245f2adfe85e58"

EXTRA_OECONF = "--with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
                --with-apr-util=${STAGING_BINDIR_CROSS}/apu-1-config \
                --prefix=${prefix} --datadir=${datadir}/apache2 \
               "

do_install () {
    install -d ${D}${bindir} ${D}${libdir}
    cp server/gen_test_char ${D}${bindir}
    install -m 755 support/apxs ${D}${bindir}/
    install -m 755 httpd ${D}${bindir}/
    install -d ${D}${datadir}/apache2/build
    cp ${S}/build/*.mk ${D}${datadir}/apache2/build
    cp build/*.mk ${D}${datadir}/apache2/build
    cp ${S}/build/instdso.sh ${D}${datadir}/apache2/build

    install -d ${D}${includedir}/apache2
    cp ${S}/include/* ${D}${includedir}/apache2
    cp include/* ${D}${includedir}/apache2
    cp ${S}/os/unix/os.h ${D}${includedir}/apache2
    cp ${S}/os/unix/unixd.h ${D}${includedir}/apache2

    cp support/envvars-std ${D}${bindir}/envvars
    chmod 755 ${D}${bindir}/envvars
}

