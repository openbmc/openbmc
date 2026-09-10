SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause AND Python-2.0 AND Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9a6c29079fc90c29d80332f44d2625f2"

SRC_URI[sha256sum] = "5e0811a4383b21dc5838069f801c4fb62113b7447663d2530d2bd6e77b49bf15"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output python3-hypothesis"

CVE_PRODUCT = "kjd:idna kjd:internationalized_domain_names_in_applications"

BBCLASSEXTEND = "native nativesdk"
