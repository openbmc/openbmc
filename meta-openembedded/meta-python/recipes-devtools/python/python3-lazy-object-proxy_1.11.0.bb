SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b44e35194fc96f303ff4302a2a5759dd"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "18874411864c9fbbbaa47f9fc1dd7aea754c86cfde21278ef427639d1dd78e9c"

PYPI_PACKAGE = "lazy_object_proxy"

inherit pypi python_setuptools_build_meta
