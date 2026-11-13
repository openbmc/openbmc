SUMMARY = "A behavior-driven development framework, Python style"
HOMEPAGE = "https://github.com/behave/behave"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1fca0a9c7e4e4148d675b4dafd5c1e80"

PV .= "+git${SRCREV}"
SRCREV = "aca602b4410163fe4be045ce86c9d76ec34a717a"
SRC_URI += "git://github.com/behave/behave;branch=main;protocol=https"


inherit python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += " \
    python3-cucumber-expressions \
    python3-cucumber-tag-expressions \
    python3-parse \
    python3-parse-type \
    python3-setuptools \
    "
