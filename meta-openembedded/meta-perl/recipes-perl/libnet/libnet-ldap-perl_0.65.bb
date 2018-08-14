SUMMARY = "LDAP Perl module"
DESCRIPTION = "Net::LDAP is a collection of modules that implements \
a LDAP services API for Perl programs. The module may be used to \
search directories or perform maintenance functions such as adding, \
deleting or modifying entries."

SECTION = "libs"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=5;md5=4d6588c2fa0d38ae162f6314d201d89e"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MA/MARSCHAP/perl-ldap-${PV}.tar.gz"

SRC_URI[md5sum] = "d057c8db76913d95c0e460c7bdd98b27"
SRC_URI[sha256sum] = "5f57dd261dc16ebf942a272ddafe69526598df71151a51916edc37a4f2f23834"

S = "${WORKDIR}/perl-ldap-${PV}"

inherit cpan

do_configure_prepend() {
    perl -pi -e 's/auto_install_now.*//g' Makefile.PL
}

RDEPENDS_${PN} = "perl \
    libconvert-asn1-perl \
    libio-socket-ssl-perl \
    libauthen-sasl-perl \
"
