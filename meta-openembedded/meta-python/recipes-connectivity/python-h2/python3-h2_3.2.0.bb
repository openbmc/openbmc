DESCRIPTION = "HTTP/2 State-Machine based protocol implementation"
HOMEPAGE = "https://github.com/python-hyper/hyper-h2"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b6b2f6bbe76528af543242d606c14851"

SRC_URI[md5sum] = "197a99c09f344a0dd987fab9801dc8d0"
SRC_URI[sha256sum] = "875f41ebd6f2c44781259005b157faed1a5031df3ae5aa7bcb4628a6c0782f14"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-hpack ${PYTHON_PN}-hyperframe"
