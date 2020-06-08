SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://setup.py;endline=7;md5=1c177d169db050341d3b890c69fb80e3"

SRC_URI[md5sum] = "fc8a614367f7ba0d34a02fd08c535afc"
SRC_URI[sha256sum] = "53eaed412477c836e1b9522c19858a8557d6e595077830146182225613b11a75"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
