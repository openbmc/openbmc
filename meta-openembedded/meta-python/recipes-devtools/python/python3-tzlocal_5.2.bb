SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "8d399205578f1a9342816409cc1e46a93ebd5755e39ea2d85334bea911bf0e6e"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-logging \
"
