DESCRIPTION = "A library of C++ classes for flexible logging to files, syslog, IDSA and other destinations."
HOMEPAGE = "http://sourceforge.net/projects/log4cpp/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://downloads.sourceforge.net/${BPN}/${BP}.tar.gz \
           file://fix-pc.patch;striplevel=2 \
          "
SRC_URI[md5sum] = "b9e2cee932da987212f2c74b767b4d8b"
SRC_URI[sha256sum] = "2cbbea55a5d6895c9f0116a9a9ce3afb86df383cd05c9d6c1a4238e5e5c8f51d"

S = "${WORKDIR}/${BPN}"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --enable-doxygen=no \
    --enable-dot=no \
    --enable-html-docs=no \
    --enable-latex-docs=no \
"
