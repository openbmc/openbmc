SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.mit;md5=f798dc4222a29fea881fa998cdf4a8c8"

SRC_URI[sha256sum] = "4c48f78f62ab596c679086084d0dd13254ae4f3d6c72a83ffdf5ebdef8f265a4"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
