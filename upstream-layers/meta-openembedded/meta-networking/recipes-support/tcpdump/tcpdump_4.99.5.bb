SUMMARY = "A sophisticated network protocol analyzer"
HOMEPAGE = "http://www.tcpdump.org/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb289217c160e2920d2e35bddc36453"

DEPENDS = "libpcap"

RDEPENDS:${PN}-ptest += " make perl \
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
    http://www.tcpdump.org/release/${BP}.tar.xz \
    file://add-ptest.patch \
    file://run-ptest \
"

SRC_URI[sha256sum] = "d76395ab82d659d526291b013eee200201380930793531515abfc6e77b4f2ee5"

UPSTREAM_CHECK_REGEX = "tcpdump-(?P<pver>\d+(\.\d+)+)\.tar"

CVE_PRODUCT = "tcpdump:tcpdump"

inherit autotools-brokensep pkgconfig ptest

PACKAGECONFIG ?= "openssl"

PACKAGECONFIG[libcap-ng] = "--with-cap-ng,--without-cap-ng,libcap-ng"
PACKAGECONFIG[openssl] = "--with-crypto,--without-crypto,openssl"
PACKAGECONFIG[smi] = "--with-smi,--without-smi,libsmi"
# Note: CVE-2018-10103 (SMB - partially fixed, but SMB printing disabled)
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb"

EXTRA_AUTORECONF += "--exclude=aclocal"

do_install:append() {
    # make install installs an unneeded extra copy of the tcpdump binary
    rm ${D}${bindir}/tcpdump.${PV}
}

do_compile_ptest() {
    oe_runmake buildtest-TESTS
}
