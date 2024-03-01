SUMMARY = "Python interface to Pocketsphinx libraries."
DESCRIPTION = "Pythonsphinx is an Open source large vocabulary, speaker-independent continuous speech recognition engines."
HOMEPAGE = "https://github.com/cmusphinx/pocketsphinx"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause | BSD-3-Clause | MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4d720cdc9c6953091f65e8b01524bb4"

SRC_URI += "file://0001-cython-Use-cythyon3-in-cmake-check.patch"
SRC_URI[sha256sum] = "27f4de0ca2d2bce391ce87eaab84fe6f0bc059b306fd1702d5fe6549b66e1586"

inherit cmake pkgconfig pypi python3native

DEPENDS += "swig-native python3-cython-native python3-scikit-build-native gstreamer1.0 gstreamer1.0-plugins-base"

EXTRA_OECMAKE += "-DBUILD_GSTREAMER=ON"

PACKAGES += "${PN}-models ${MLPREFIX}gstreamer1.0-pocketsphinx"

FILES:${MLPREFIX}gstreamer1.0-pocketsphinx = "${libdir}/gstreamer-1.0/"
FILES:${PN}-models = "${datadir}/pocketsphinx/model"
