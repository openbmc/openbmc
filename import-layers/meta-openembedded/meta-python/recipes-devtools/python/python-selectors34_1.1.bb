SUMMARY = "Backport of the selectors module from Python 3.4"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=1cfbbf590d8d5b7fe937516217b778b3"

SRC_URI[md5sum] = "403194b10f35a5258e0642712fdd3753"
SRC_URI[sha256sum] = "84b3743b9046461aebbcd13c15e79ab91e79acfb6e030b54a0ec6360ae0bbc52"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-six \
    "    
