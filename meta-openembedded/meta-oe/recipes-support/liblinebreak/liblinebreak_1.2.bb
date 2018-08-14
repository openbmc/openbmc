DESCRIPTION = "Liblinebreak is an implementation of the line breaking algorithm as described in Unicode 5.1.0 Standard Annex 14, Revision 22"
HOMEPAGE = "http://vimgadgets.sourceforge.net/liblinebreak/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENCE;md5=3715191da62bafb5cfc4ff36195b2ec3"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/vimgadgets/liblinebreak/${PV}/liblinebreak-${PV}.tar.gz"
SRC_URI[md5sum] = "d18039259001ccb24b5dd4648c49c5ad"
SRC_URI[sha256sum] = "9efcb0cb1afc75ad1e92d2b2dbf4d9c77b072d6656c5f1a150af8b718d0c7b76"

inherit autotools
