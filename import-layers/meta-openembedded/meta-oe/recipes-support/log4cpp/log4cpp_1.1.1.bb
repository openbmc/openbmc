DESCRIPTION = "A library of C++ classes for flexible logging to files, syslog, IDSA and other destinations."
HOMEPAGE = "http://sourceforge.net/projects/log4cpp/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI[md5sum] = "1e173df8ee97205f412ff84aa93b8fbe"
SRC_URI[sha256sum] = "35abf332630a6809c969276b1d60b90c81a95daf24c86cfd7866ffef72f9bed0"

SRC_URI = "http://downloads.sourceforge.net/${BPN}/${BP}.tar.gz \
           file://fix-pc.patch;striplevel=2 \
          "

S = "${WORKDIR}/${BPN}"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --enable-doxygen=no \
    --enable-dot=no \
    --enable-html-docs=no \
    --enable-latex-docs=no \
    LDFLAGS=-pthread \
"
