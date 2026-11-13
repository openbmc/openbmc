SUMMARY = "IO-stringy - I/O on in-core objects like strings and arrays"
DESCRIPTION = "This toolkit primarily provides modules for performing both \
traditional and object-oriented i/o on things *other* than normal \
filehandles; in particular, IO::Scalar, IO::ScalarArray, and IO::Lines."

HOMEPAGE = "http://www.zeegee.com/products/IO-stringy/"
SECTION = "devel"
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=01406e4ff2e60d88d42ef1caebdd0011"


SRC_URI = "${CPAN_MIRROR}/authors/id/C/CA/CAPOEIRAB/IO-Stringy-${PV}.tar.gz"
SRC_URI[sha256sum] = "872328f5a665c79e24a777b35e4f7fc395a1912d5e142879ffda83e8c102f765"

S = "${UNPACKDIR}/IO-Stringy-${PV}"

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
