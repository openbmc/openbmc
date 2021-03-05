SUMMARY = "A sophisticated network protocol analyzer"
HOMEPAGE = "http://www.tcpdump.org/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb289217c160e2920d2e35bddc36453"

DEPENDS = "libpcap"

RDEPENDS_${PN}-ptest += " make perl \
	perl-module-file-basename \
	perl-module-file-spec \
	perl-module-file-spec-unix \
	perl-module-file-path \
	perl-module-file-glob \
	perl-module-data-dumper \
	perl-module-bytes \
	perl-module-posix \
	perl-module-carp \
	perl-module-cwd \
    perl-module-constant \
"

SRC_URI = " \
    http://www.tcpdump.org/release/${BP}.tar.gz \
    file://add-ptest.patch \
    file://run-ptest \
    file://0001-aclocal.m4-Skip-checking-for-pcap-config.patch \
"

SRC_URI[md5sum] = "b10aa2f497def7283bc060f626879ce5"
SRC_URI[sha256sum] = "8cf2f17a9528774a7b41060323be8b73f76024f7778f59c34efa65d49d80b842"

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
    rm ${D}${bindir}/tcpdump.${PV}
}

do_compile_ptest() {
    oe_runmake buildtest-TESTS
}
