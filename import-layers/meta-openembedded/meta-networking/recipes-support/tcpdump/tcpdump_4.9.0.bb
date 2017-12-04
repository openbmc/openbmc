SUMMARY = "A sophisticated network protocol analyzer"
HOMEPAGE = "http://www.tcpdump.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d4b0366557951c84a94fabe3529f867"
SECTION = "net"
DEPENDS = "libpcap"

SRC_URI = " \
    http://www.tcpdump.org/release/${BP}.tar.gz \
    file://unnecessary-to-check-libpcap.patch \
    file://tcpdump-configure-dlpi.patch \
    file://add-ptest.patch \
    file://run-ptest \
"

SRC_URI[md5sum] = "2b83364eef53b63ca3181b4eb56dab0c"
SRC_URI[sha256sum] = "eae98121cbb1c9adbedd9a777bf2eae9fa1c1c676424a54740311c8abcee5a5e"

export LIBS=" -lpcap"

inherit autotools-brokensep ptest
CACHED_CONFIGUREVARS = "ac_cv_linux_vers=${ac_cv_linux_vers=2}"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto=yes, --without-openssl --without-crypto, openssl"
PACKAGECONFIG[smi] = "--with-smi, --without-smi,libsmi"
PACKAGECONFIG[libcap-ng] = "--with-cap-ng=yes,--with-cap-ng=no,libcap-ng"

EXTRA_AUTORECONF += " -I m4"

do_configure_prepend() {
    mkdir -p ${S}/m4
    if [ -f aclocal.m4 ]; then
        mv aclocal.m4 ${S}/m4
    fi
    # AC_CHECK_LIB(dlpi.. was looking to host /lib
    sed -i 's:-L/lib::g' ./configure.in
}
do_configure_append() {
    sed -i 's:-L/usr/lib::' ./Makefile
    sed -i 's:-Wl,-rpath,${STAGING_LIBDIR}::' ./Makefile
    sed -i 's:-I/usr/include::' ./Makefile
}

do_install_append() {
    # tcpdump 4.0.0 installs a copy to /usr/sbin/tcpdump.4.0.0
    rm -f ${D}${sbindir}/tcpdump.${PV}
}

do_compile_ptest() {
    oe_runmake buildtest-TESTS
}
