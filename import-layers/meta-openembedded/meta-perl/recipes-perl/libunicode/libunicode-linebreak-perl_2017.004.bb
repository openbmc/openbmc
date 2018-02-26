SUMMARY = "Unicode::LineBreak - UAX #14 Unicode Line Breaking Algorithm."
DESCRIPTION = "Unicode::LineBreak performs Line Breaking Algorithm described in Unicode \
Standard Annex #14 [UAX #14]. East_Asian_Width informative property \
defined by Annex #11 [UAX #11] will be concerned to determine breaking \
positions."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~nezumi/Unicode-LineBreak-${PV}/"

LICENSE = "Artistic-1.0 | GPLv1+"
LIC_FILES_CHKSUM = "file://README;md5=77241abd74fec561b3f3de1b44c0241b"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEZUMI/Unicode-LineBreak-${PV}.tar.gz"

SRC_URI[md5sum] = "de7672227922260ac92d20bbad29660b"
SRC_URI[sha256sum] = "655bc3c4cb60ad0770d97816716cfe322f24e602c70e595f5941dfa02c40cb76"

S = "${WORKDIR}/Unicode-LineBreak-${PV}"

DEPENDS = "libsombok3 libmime-charset-perl"

RDEPENDS_${PN} = "libsombok3 libmime-charset-perl"

inherit cpan

BBCLASSEXTEND = "native"
