SUMMARY = "moteus brushless controller library and tools"
HOMEPAGE = "https://github.com/mjbots/moteus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=3;endline=9;md5=24025d3c660abfc62a83f0e709a45e76"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fae569f0c2e208059e081cf0fcca2e8f71c6c1de582836c745bb884af1e35bf4"

S = "${WORKDIR}/moteus-${PV}"

RDEPENDS:${PN} += "\
    python3-can \
    python3-importlib-metadata \
    python3-pyelftools \
    python3-pyserial \
"
