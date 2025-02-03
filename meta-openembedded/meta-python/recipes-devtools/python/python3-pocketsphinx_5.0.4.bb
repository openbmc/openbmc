SUMMARY = "Python interface to Pocketsphinx libraries."
DESCRIPTION = "Pythonsphinx is an Open source large vocabulary, speaker-independent continuous speech recognition engines."
HOMEPAGE = "https://github.com/cmusphinx/pocketsphinx"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause | BSD-3-Clause | MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4d720cdc9c6953091f65e8b01524bb4"

SRC_URI += "file://0001-cython-Use-cythyon3-in-cmake-check.patch"
SRC_URI[sha256sum] = "17d2971e998490def9554b6ba41886708422af5b4ae888723b1b077aab0fe8d4"

inherit cmake pkgconfig pypi python3native cython

DEPENDS += "swig-native python3-scikit-build-native gstreamer1.0 gstreamer1.0-plugins-base"

EXTRA_OECMAKE += "-DBUILD_GSTREAMER=ON"

PACKAGES += "${PN}-models ${MLPREFIX}gstreamer1.0-pocketsphinx"

FILES:${MLPREFIX}gstreamer1.0-pocketsphinx = "${libdir}/gstreamer-1.0/"
FILES:${PN}-models = "${datadir}/pocketsphinx/model"
