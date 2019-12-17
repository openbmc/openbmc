SUMMARY = "LDAP Perl module"
DESCRIPTION = "Net::LDAP is a collection of modules that implements \
a LDAP services API for Perl programs. The module may be used to \
search directories or perform maintenance functions such as adding, \
deleting or modifying entries."

SECTION = "libs"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=5;md5=4d6588c2fa0d38ae162f6314d201d89e"

SRC_URI = "${CPAN_MIRROR}/authors/id/M/MA/MARSCHAP/perl-ldap-${PV}.tar.gz"

SRC_URI[md5sum] = "c4c1ae9299cd488e75c1b82904458bef"
SRC_URI[sha256sum] = "09263ce6166e80c98d689d41d09995b813389fd069b784601f6dc57f8e2b4102"

S = "${WORKDIR}/perl-ldap-${PV}"

inherit cpan ptest-perl

do_configure_prepend() {
    perl -pi -e 's/auto_install_now.*//g' Makefile.PL
}

do_install_ptest() {
	cp -r ${B}/data ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS_${PN} += " \
    libconvert-asn1-perl \
    libio-socket-ssl-perl \
    libauthen-sasl-perl \
    perl-module-integer \
"

RDEPENDS_${PN}-ptest += " \
    libxml-sax-base-perl \
    libxml-sax-writer-perl \
    perl-module-file-compare \
    perl-module-perlio \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
