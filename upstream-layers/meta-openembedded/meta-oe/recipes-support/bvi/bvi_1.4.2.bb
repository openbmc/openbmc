SUMMARY = "Binary VI editor"
DESCRIPTION = "bvi is a visual editor for binary files."
HOMEPAGE = "https://sourceforge.net/projects/bvi"
SECTION = "console/utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a36207309d382da27cd66fdaae922e3c"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz"
SRC_URI[sha256sum] = "4bba16c2b496963a9b939336c0abcc8d488664492080ae43a86da18cf4ce94f2"

DEPENDS += "ncurses"

# The project uses old style C interfaces and it is not compatible with C23
# it builds fine with C17, specifying gnu17 as the C dialect to use
CFLAGS:append = " -std=gnu17 -Wno-old-style-definition"

inherit pkgconfig autotools-brokensep
