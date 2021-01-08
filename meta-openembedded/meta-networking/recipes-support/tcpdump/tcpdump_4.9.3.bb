SUMMARY = "A sophisticated network protocol analyzer"
HOMEPAGE = "http://www.tcpdump.org/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d4b0366557951c84a94fabe3529f867"

DEPENDS = "libpcap"

RDEPENDS_${PN}-ptest += " make perl \
	perl-module-file-basename \
	perl-module-posix \
	perl-module-carp"

SRC_URI = " \
    http://www.tcpdump.org/release/${BP}.tar.gz \
    file://unnecessary-to-check-libpcap.patch \
    file://avoid-absolute-path-when-searching-for-libdlpi.patch \
    file://add-ptest.patch \
    file://run-ptest \
    file://0001-PPP-When-un-escaping-don-t-allocate-a-too-large-buff.patch \
"

SRC_URI[md5sum] = "a4ead41d371f91aa0a2287f589958bae"
SRC_URI[sha256sum] = "2cd47cb3d460b6ff75f4a9940f594317ad456cfbf2bd2c8e5151e16559db6410"

UPSTREAM_CHECK_REGEX = "tcpdump-(?P<pver>\d+(\.(?!99)\d+)+)\.tar"

inherit autotools-brokensep ptest

PACKAGECONFIG ?= "openssl"

PACKAGECONFIG[libcap-ng] = "--with-cap-ng,--without-cap-ng,libcap-ng"
PACKAGECONFIG[openssl] = "--with-crypto,--without-crypto,openssl"
PACKAGECONFIG[smi] = "--with-smi,--without-smi,libsmi"
# Note: CVE-2018-10103 (SMB - partially fixed, but SMB printing disabled)
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb"

EXTRA_AUTORECONF += "-I m4"

do_configure_prepend() {
    mkdir -p ${S}/m4
    if [ -f aclocal.m4 ]; then
        mv aclocal.m4 ${S}/m4
    fi
}

do_install_append() {
    # make install installs an unneeded extra copy of the tcpdump binary
    rm -f ${D}${sbindir}/tcpdump.${PV}
}

do_compile_ptest() {
    oe_runmake buildtest-TESTS
}
