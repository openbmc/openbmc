SUMMARY = "A behavior-driven development framework, Python style"
HOMEPAGE = "https://github.com/behave/behave"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1fca0a9c7e4e4148d675b4dafd5c1e80"

PV .= "+git${SRCREV}"
SRCREV = "7673da8324be5588642b23511ca4d7a134a0b0c1"
SRC_URI += "git://github.com/behave/behave;branch=release/v1.3.x;protocol=https"


inherit python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-parse-type \
    python3-setuptools \
    python3-six \
    python3-cucumber-tag-expressions \
    "
