SUMMARY = "An enhanced version of the tty module"
SECTION = "devel/python"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d90e2d280a4836c607520383d1639be1"

SRC_URI[sha256sum] = "2cca4cf5f83035ca12627c4bbeff2891ad4711666247a790fd8200d73f38c3f0"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-io \
"
