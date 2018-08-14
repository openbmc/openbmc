SUMMARY = "Interpret and act on wait() status values"
DESCRIPTION = "This module contains functions for interpreting and acting \
on wait status values."

HOMEPAGE = "http://search.cpan.org/~rosch/Proc-WaitStat/"
SECTION = "libraries"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://README;beginline=21;endline=23;md5=f36550f59a0ae5e6e3b0be6a4da60d26"

RDEPENDS_${PN} += "perl libipc-signal-perl"

S = "${WORKDIR}/Proc-WaitStat-${PV}"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/R/RO/ROSCH/Proc-WaitStat-${PV}.tar.gz"

SRC_URI[md5sum] = "b911bd579b6b142391b21de1efa30c95"
SRC_URI[sha256sum] = "d07563f5e787909d16e7390241e877f49ab739b1de9d0e2ea1a41bd0bf4474bc"

inherit cpan
