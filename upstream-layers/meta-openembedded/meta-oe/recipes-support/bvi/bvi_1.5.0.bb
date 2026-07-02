SUMMARY = "Binary VI editor"
DESCRIPTION = "bvi is a visual editor for binary files."
HOMEPAGE = "https://sourceforge.net/projects/bvi"
SECTION = "console/utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a36207309d382da27cd66fdaae922e3c"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz"
SRC_URI[sha256sum] = "6540716a1a3b2b9711635108da14b26baea488881d4a682121c0bddbba6b74cb"

DEPENDS += "ncurses"

# The project uses old style C interfaces and it is not compatible with C23
# it builds fine with C17, specifying gnu17 as the C dialect to use
CFLAGS:append = " -std=gnu17 -Wno-old-style-definition"

inherit pkgconfig autotools-brokensep
