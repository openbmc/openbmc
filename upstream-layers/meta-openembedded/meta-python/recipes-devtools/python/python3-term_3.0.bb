SUMMARY = "An enhanced version of the tty module"
SECTION = "devel/python"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d90e2d280a4836c607520383d1639be1"

SRC_URI[sha256sum] = "a6a81535e68fd98d25b1e2a1dff15400c5678e7f942e2d5bb5bde56784534d98"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} = "\
    python3-io \
"
