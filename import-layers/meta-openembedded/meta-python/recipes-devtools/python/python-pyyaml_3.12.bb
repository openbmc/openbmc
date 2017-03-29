SUMMARY = "Python support for YAML"
HOMEPAGE = "http://www.pyyaml.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"
DEPENDS = "libyaml python-cython-native"

SRC_URI = "http://pyyaml.org/download/pyyaml/PyYAML-${PV}.tar.gz \
           file://setup.py \
"

SRC_URI[md5sum] = "4c129761b661d181ebf7ff4eb2d79950"
SRC_URI[sha256sum] = "592766c6303207a20efc445587778322d7f73b161bd994f227adaa341ba212ab"

S = "${WORKDIR}/PyYAML-${PV}"

inherit distutils

do_configure_prepend() {
    # upstream setup.py overcomplicated, use ours
    install -m 0644 ${WORKDIR}/setup.py ${S}
}

BBCLASSEXTEND = "native"
