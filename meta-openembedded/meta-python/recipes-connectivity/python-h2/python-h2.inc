DESCRIPTION = "HTTP/2 State-Machine based protocol implementation"
HOMEPAGE = "https://github.com/python-hyper/hyper-h2"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b6b2f6bbe76528af543242d606c14851"

SRC_URI[md5sum] = "950b5a62a2a608dc4547a01edf99aa8f"
SRC_URI[sha256sum] = "b8a32bd282594424c0ac55845377eea13fa54fe4a8db012f3a198ed923dc3ab4"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-hpack ${PYTHON_PN}-hyperframe"
