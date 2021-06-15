DESCRIPTION = "HTTP/2 State-Machine based protocol implementation"
HOMEPAGE = "https://github.com/python-hyper/hyper-h2"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=aa3b9b4395563dd427be5f022ec321c1"

SRC_URI[md5sum] = "d086f6a9746a5f4eeb63bb3d0a482e1f"
SRC_URI[sha256sum] = "bb7ac7099dd67a857ed52c815a6192b6b1f5ba6b516237fc24a085341340593d"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-hpack ${PYTHON_PN}-hyperframe"
