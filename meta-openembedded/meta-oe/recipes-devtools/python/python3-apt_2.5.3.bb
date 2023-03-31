SUMMARY = "Python-apt is a wrapper to use features of apt from python."
LICENSE = "GPL-2.0-only & FSFAP"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://debian/copyright;md5=4ed7b6862ca422678b17e7d4ed592285"

SRC_URI = "git://salsa.debian.org/apt-team/python-apt.git;protocol=https;branch=main"

SRCREV = "3252935b6224b3bd2e2250894b9559b47c61b2e1"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "apt"
RDEPENDS:${PN} += "apt python3-core"

FILES:${PN} = "${libdir} ${datadir}/python-apt"
