SUMMARY = "moteus brushless controller library and tools"
HOMEPAGE = "https://github.com/mjbots/moteus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=c2d9643b4523fdf462545aeb1356ad23"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "d66b00328e8cb3f57adfb54f43c6acb6d09c2215808c242725ece530fc7f9fdf"

S = "${UNPACKDIR}/moteus-${PV}"

RDEPENDS:${PN} += "\
    python3-can \
    python3-importlib-metadata \
    python3-pyelftools \
    python3-pyserial \
"
