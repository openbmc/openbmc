SUMMARY = "Hardware RNG based on CPU timing jitter"
DESCRIPTION = "The Jitter RNG provides a noise source using the CPU execution timing jitter. \
It does not depend on any system resource other than a high-resolution time \
stamp. It is a small-scale, yet fast entropy source that is viable in almost \
all environments and on a lot of CPU architectures."
HOMEPAGE = "http://www.chronox.de/jent.html"
LICENSE = "GPLv2+ | BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e52365752b36cfcd7f9601d80de7d8c6 \
                    file://COPYING.gplv2;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.bsd;md5=66a5cedaf62c4b2637025f049f9b826f \
                   "
SRC_URI = "git://github.com/smuellerDD/jitterentropy-library.git \
           file://0001-fix-do_install-failure-on-oe.patch \
          "
SRCREV = "f5a80c6f3fcc6deebd0eabf75324f48aed1afbce"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"

LDFLAGS += "-Wl,-O0"

do_install () {
    oe_runmake install INCDIR="/include" \
                       DESTDIR="${D}" \
                       PREFIX="${exec_prefix}" \
                       LIBDIR="${baselib}"
}

