SUMMARY = "A behavior-driven development framework, Python style"
HOMEPAGE = "https://github.com/behave/behave"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d950439e8ea6ed233e4288f5e1a49c06"

PV .= "+git${SRCREV}"
SRCREV = "9520119376046aeff73804b5f1ea05d87a63f370"
SRC_URI += "git://github.com/behave/behave;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-parse-type \
    python3-setuptools \
    python3-six \
    "
