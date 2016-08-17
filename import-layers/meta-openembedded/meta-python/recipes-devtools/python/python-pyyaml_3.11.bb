SUMMARY = "Python support for YAML"
HOMEPAGE = "http://www.pyyaml.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"
DEPENDS = "libyaml python-cython-native"

SRC_URI = "http://pyyaml.org/download/pyyaml/PyYAML-${PV}.tar.gz \
           file://setup.py \
"

SRC_URI[md5sum] = "f50e08ef0fe55178479d3a618efe21db"
SRC_URI[sha256sum] = "c36c938a872e5ff494938b33b14aaa156cb439ec67548fcab3535bb78b0846e8"

S = "${WORKDIR}/PyYAML-${PV}"

inherit distutils

do_configure_prepend() {
    # upstream setup.py overcomplicated, use ours
    install -m 0644 ${WORKDIR}/setup.py ${S}
}

BBCLASSEXTEND = "native"
