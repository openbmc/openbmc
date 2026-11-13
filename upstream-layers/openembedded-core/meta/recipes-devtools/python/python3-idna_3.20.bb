SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause AND Python-2.0 AND Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9a6c29079fc90c29d80332f44d2625f2"

SRC_URI[sha256sum] = "a7db850025b95ded1eae8a46181a1a6c56c92c96f0e2b005d9ff8dc0210cab44"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-hypothesis"

CVE_PRODUCT = "kjd:idna kjd:internationalized_domain_names_in_applications"

BBCLASSEXTEND = "native nativesdk"
