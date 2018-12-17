DESCRIPTION = "The Apache HTTP Server is a powerful, efficient, and \
extensible web server."
SUMMARY = "Apache HTTP Server"
HOMEPAGE = "http://httpd.apache.org/"
DEPENDS = "expat-native pcre-native apr-native apr-util-native"
SECTION = "net"
LICENSE = "Apache-2.0"

inherit autotools pkgconfig native

SRC_URI = "${APACHE_MIRROR}/httpd/httpd-${PV}.tar.bz2 \
           file://0001-configure-use-pkg-config-for-PCRE-detection.patch \
           file://CVE-2018-11763.patch \
          "

S = "${WORKDIR}/httpd-${PV}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d52d0fd0bc788f068e647116c01ddfcd"
SRC_URI[md5sum] = "818adca52f3be187fe45d6822755be95"
SRC_URI[sha256sum] = "fa53c95631febb08a9de41fd2864cfff815cf62d9306723ab0d4b8d7aa1638f0"

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

