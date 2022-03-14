SUMMARY = "Hardware RNG based on CPU timing jitter"
DESCRIPTION = "The Jitter RNG provides a noise source using the CPU execution timing jitter. \
It does not depend on any system resource other than a high-resolution time \
stamp. It is a small-scale, yet fast entropy source that is viable in almost \
all environments and on a lot of CPU architectures."
HOMEPAGE = "http://www.chronox.de/jent.html"
LICENSE = "GPLv2+ | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c94a9d191202a5552f381a023551396 \
                    file://LICENSE.gplv2;md5=eb723b61539feef013de476e68b5c50a \
                    file://LICENSE.bsd;md5=66a5cedaf62c4b2637025f049f9b826f \
                    "
SRC_URI = "git://github.com/smuellerDD/jitterentropy-library.git;branch=master;protocol=https"
SRCREV = "887c9871ea110e397812ff7f3b28a6269f0a2ffc"
S = "${WORKDIR}/git"

# remove at next version upgrade or when output changes
HASHEQUIV_HASH_VERSION .= ".2"

do_configure[noexec] = "1"

LDFLAGS += "-Wl,-O0"

do_install () {
    oe_runmake install INCDIR="/include" \
                       DESTDIR="${D}" \
                       PREFIX="${exec_prefix}" \
                       LIBDIR="${baselib}" \
                       INSTALL_STRIP="install"
}

