SUMMARY = "Target packages for Qt SDK"

QTLIBPREFIX = ""

require packagegroup-qt-toolchain-target.inc

inherit distro_features_check
# depends on qt4-x11-free
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} += " \
        qt4-x11-free-dev \
        ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'libqtopengl4-dev', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'openvg', 'libqtopenvg4-dev', '', d)} \
        "
