require gnuplot.inc

SRC_URI = "${SOURCEFORGE_MIRROR}/gnuplot/${BP}.tar.gz;name=archive \
    http://www.mneuroth.de/privat/zaurus/qtplot-0.2.tar.gz;name=qtplot \
    file://subdirs.patch \
    file://lua-loadlibs-configure-in-fix.patch \
    file://gnuplot.desktop \
    file://gnuplot.png \
"

SRC_URI[archive.md5sum] = "c5e96fca73afbee4f57cbc1bfce6b3b8"
SRC_URI[archive.sha256sum] = "25f3e0bf192e01115c580f278c3725d7a569eb848786e12b455a3fda70312053"
SRC_URI[qtplot.md5sum] = "0a481885a496092c77eb4017540b5cf6"
SRC_URI[qtplot.sha256sum] = "6df317183ff62cc82f3dcf88207a267cd6478cb5147f55d7530c94f1ad5f4132"
