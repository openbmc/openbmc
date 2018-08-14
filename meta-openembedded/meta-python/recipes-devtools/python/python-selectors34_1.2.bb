SUMMARY = "Backport of the selectors module from Python 3.4"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=2fae0222c31d6c10488d4ab93a863af7"

SRC_URI[md5sum] = "bc855a1c8839a811476c019dc07d92dd"
SRC_URI[sha256sum] = "09f5066337f8a76fb5233f267873f89a27a17c10bf79575954894bb71686451c"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-six \
    "    
