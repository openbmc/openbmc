SUMMARY = "Hash table for C structures"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=564f9c44927f6247dc810bf557e2b240"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2"

SRC_URI[md5sum] = "1f14bbee7ee73ed0ceb3549f8cf378b4"
SRC_URI[sha256sum] = "956f5c99798349c413275fe4c9ff128d72e280655dadbe4365f8e9fbda91393f"

do_install () {
    install -dm755 ${D}${includedir}
    install -m 0644 ${S}/src/*.h ${D}${includedir}
}

BBCLASSEXTEND = "native"
