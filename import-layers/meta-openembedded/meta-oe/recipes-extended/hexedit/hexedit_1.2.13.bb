SUMMARY = "view and edit files in hexadecimal or in ASCII"
HOMEPAGE = "http://rigaux.org/hexedit.html"
SECTION = "console/utils"
LICENSE = "GPLv2+"
DEPENDS = "ncurses"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://rigaux.org/${BP}.src.tgz \
    file://0001-don-t-strip-when-installing.patch "

SRC_URI[md5sum] = "a5af1378d028512a9cad27a5ba3e15f9"
SRC_URI[sha256sum] = "6a126da30a77f5c0b08038aa7a881d910e3b65d13767fb54c58c983963b88dd7"

inherit autotools-brokensep

S = "${WORKDIR}/${BPN}"
