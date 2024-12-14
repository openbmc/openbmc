SUMMARY = "LDAP Perl module"
DESCRIPTION = "Net::LDAP is a collection of modules that implements \
a LDAP services API for Perl programs. The module may be used to \
search directories or perform maintenance functions such as adding, \
deleting or modifying entries."

SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=5;md5=4d6588c2fa0d38ae162f6314d201d89e"

SRC_URI = "${CPAN_MIRROR}/authors/id/M/MA/MARSCHAP/perl-ldap-${PV}.tar.gz"

SRC_URI[sha256sum] = "e2f389fe3e7a9e4b61488692919ad723b98f3b479b5288f610daa8c27995b351"

S = "${WORKDIR}/perl-ldap-${PV}"

inherit cpan ptest-perl

do_configure:prepend() {
    perl -pi -e 's/auto_install_now.*//g' Makefile.PL
}

do_install_ptest() {
	cp -r ${B}/data ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS:${PN} += " \
    libconvert-asn1-perl \
    libio-socket-ssl-perl \
    libauthen-sasl-perl \
    perl-module-integer \
"

RDEPENDS:${PN}-ptest += " \
    libxml-sax-base-perl \
    libxml-sax-writer-perl \
    perl-module-file-compare \
    perl-module-perlio \
    perl-module-test-more \
"
