require gnuplot.inc

SRC_URI = "${SOURCEFORGE_MIRROR}/gnuplot/${BP}.tar.gz;name=archive \
    http://www.mneuroth.de/privat/zaurus/qtplot-0.2.tar.gz;name=qtplot \
    file://subdirs.patch \
    file://lua-loadlibs-configure-in-fix.patch \
    file://gnuplot.desktop \
    file://gnuplot.png \
"

SRC_URI[archive.md5sum] = "9a476b21f74bd99c876f1509d731a0f9"
SRC_URI[archive.sha256sum] = "e550f030c7d04570e89c3d4e3f6e82296816508419c86ab46c4dd73156519a2d"
SRC_URI[qtplot.md5sum] = "0a481885a496092c77eb4017540b5cf6"
SRC_URI[qtplot.sha256sum] = "6df317183ff62cc82f3dcf88207a267cd6478cb5147f55d7530c94f1ad5f4132"
