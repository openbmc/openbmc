SUMMARY = "Perl module for provides screen dump of Perl data"

HOMEPAGE = "http://search.cpan.org/~flora/Dumpvalue/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f736bec5ada1fc5e39b2a8e7e06bbcbb"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/F/FL/FLORA/Dumpvalue-${PV}.tar.gz"

SRC_URI[md5sum] = "6ede9f693d4a9c4555541cb1a1cc2006"
SRC_URI[sha256sum] = "9ea74606b545f769a787ec2ae229549a2ad0a8e3cd4b14eff2ce3841836b3bdb"

S = "${WORKDIR}/Dumpvalue-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
