SUMMARY = "Hardware RNG based on CPU timing jitter"
DESCRIPTION = "The Jitter RNG provides a noise source using the CPU execution timing jitter. \
It does not depend on any system resource other than a high-resolution time \
stamp. It is a small-scale, yet fast entropy source that is viable in almost \
all environments and on a lot of CPU architectures."
HOMEPAGE = "http://www.chronox.de/jent.html"
LICENSE = "GPL-2.0-or-later | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18a5cada7ee95e61db4180f0cb4a69cf \
                    file://LICENSE.gplv2;md5=eb723b61539feef013de476e68b5c50a \
                    file://LICENSE.bsd;md5=66a5cedaf62c4b2637025f049f9b826f \
                    "
SRC_URI = "git://github.com/smuellerDD/jitterentropy-library.git;branch=master;protocol=https"
SRCREV = "7199c8959347b0e8342df7dc6e0c1fc4b484d2fb"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"

LDFLAGS += "-Wl,-O0"

do_install () {
    oe_runmake install INCDIR="/include" \
                       DESTDIR="${D}" \
                       PREFIX="${exec_prefix}" \
                       LIBDIR="${baselib}" \
                       INSTALL_STRIP="install"
}

