SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://parse.py;beginline=1273;endline=1291;md5=5147afdd9b3615290ad8733f0137a1a1"

SRC_URI[md5sum] = "fa69ab2fe846f9b183411391f7c6897b"
SRC_URI[sha256sum] = "c3cdf6206f22aeebfa00e5b954fcfea13d1b2dc271c75806b6025b94fb490939"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "
