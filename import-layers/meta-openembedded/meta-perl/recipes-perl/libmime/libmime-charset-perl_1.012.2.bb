SUMMARY = "MIME::Charset - Charset Information for MIME."
DESCRIPTION = "MIME::Charset provides information about character sets used for MIME \
messages on Internet."
HOMEPAGE = "http://search.cpan.org/~nezumi/MIME-Charset-${PV}/"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPLv1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEZUMI/MIME-Charset-${PV}.tar.gz"

SRC_URI[md5sum] = "71440416376248c31aa3bef753fae28d"
SRC_URI[sha256sum] = "878c779c0256c591666bd06c0cde4c0d7820eeeb98fd1183082aee9a1e7b1d13"

S = "${WORKDIR}/MIME-Charset-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
