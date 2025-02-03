DESCRIPTION = "A utility provides a fake certificate authority (CA)"
HOMEPAGE = "https://pypi.org/project/trustme"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb59582a4d7ecbb79dfeefa0f69932e1"

SRC_URI[sha256sum] = "6528ba2bbc7f2db41f33825c8dd13e3e3eb9d334ba0f909713c8c3139f4ae47f"

inherit pypi python_hatchling python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-cryptography \
    python3-datetime \
    python3-idna \
    python3-io \
"

RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-pyopenssl \
    python3-pyasn1-modules \
    python3-service-identity \
    python3-six \
"
