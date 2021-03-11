DESCRIPTION = "This module is a thin wrapper for JSON::XS-compatible modules with a few \
additional features. All the backend modules convert a Perl data structure \
to a JSON text as of RFC4627 (which we know is obsolete but we still stick \
to; see below for an option to support part of RFC7159) and vice versa. \
This module uses JSON::XS by default, and when JSON::XS is not available, \
this module falls back on JSON::PP, which is in the Perl core since 5.14. \
If JSON::PP is not available either, this module then falls back on \
JSON::backportPP (which is actually JSON::PP in a different .pm file) \
bundled in the same distribution as this module. You can also explicitly \
specify to use Cpanel::JSON::XS, a fork of JSON::XS by Reini Urban."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
PR = "r0"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/JSON"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61"

SRC_URI = "https://cpan.metacpan.org/authors/id/I/IS/ISHIGAKI/JSON-2.97001.tar.gz"

SRC_URI[md5sum] = "693d6ff167496362f8ec6c3c5b8ba5ee"
SRC_URI[sha256sum] = "e277d9385633574923f48c297e1b8acad3170c69fa590e31fa466040fc6f8f5a"

S = "${WORKDIR}/JSON-${PV}"

inherit cpan allarch

BBCLASSEXTEND = "native"
