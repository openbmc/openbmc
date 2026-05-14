SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9a6c29079fc90c29d80332f44d2625f2"

SRC_URI[sha256sum] = "585ea8fe5d69b9181ec1afba340451fba6ba764af97026f92a91d4eef164a242"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output"

BBCLASSEXTEND = "native nativesdk"
