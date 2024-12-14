SUMMARY = "IO-stringy - I/O on in-core objects like strings and arrays"
DESCRIPTION = "This toolkit primarily provides modules for performing both \
traditional and object-oriented i/o on things *other* than normal \
filehandles; in particular, IO::Scalar, IO::ScalarArray, and IO::Lines."

HOMEPAGE = "http://www.zeegee.com/products/IO-stringy/"
SECTION = "devel"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=01406e4ff2e60d88d42ef1caebdd0011"


SRC_URI = "${CPAN_MIRROR}/authors/id/D/DS/DSKOLL/IO-stringy-${PV}.tar.gz"
SRC_URI[sha256sum] = "8c67fd6608c3c4e74f7324f1404a856c331dbf48d9deda6aaa8296ea41bf199d"

S = "${WORKDIR}/IO-stringy-${PV}"

inherit cpan

RPROVIDES:${PN} += " libio-atomicfile-perl \
    libio-innerfile-perl \
    libio-lines-perl \
    libio-scalar-perl \
    libio-scalararray-perl \
    libio-wrap-perl \
    libio-wraptie-perl \
    libio-wraptie-master-perl \
    libio-wraptie-slave-perl \
"

BBCLASSEXTEND = "native"
