SUMMARY = "OpenGL function pointer management library"
HOMEPAGE = "https://github.com/anholt/libepoxy/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=58ef4c80d401e07bd9ee8b6b58cf464b"


SRC_URI = " \
    git://github.com/anholt/libepoxy.git \
    file://0001-select-platforms-based-on-configuration-results.patch \
    file://0002-add-an-option-to-disable-glx-support.patch \
    file://no-need-for-python3.patch \
"
SRCREV="e2c33af5bfcfc9d168f9e776156dd47c33f428b3"
PV = "1.3.1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check
# depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "util-macros virtual/egl"

PACKAGECONFIG[x11] = "--enable-glx, --disable-glx, virtual/libx11"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
