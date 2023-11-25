SUMMARY = "Open source Python library for rapid development of applications \
    that make use of innovative user interfaces, such as multi-touch apps."
HOMEPAGE = "https://kivy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed5cd0f2cfda42852c332e3b88c85fea"

inherit setuptools3 pkgconfig features_check
PIP_INSTALL_PACKAGE = "Kivy"

# Kivy's setup files only look for GLES libraries for Android, iOS, RPi,
# and mali-based OS's. We need to patch the setup file to tell Kivy setup
# that our machine has GLES libaries installed as well
# Also, if using SDL2 as backend, SDL2 needs to be configured with gles
SRC_URI = "git://github.com/kivy/kivy.git;protocol=https;branch=stable \
           file://0001-add-support-for-glesv2.patch \
           "

SRCREV = "344768bfefd2e8f5302cc9dfb8ca41991ce6f7e7"

S = "${WORKDIR}/git"

PACKAGES += "${PN}-examples"
FILES:${PN}-examples = "/usr/share/kivy-examples"

USE_WAYLAND = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', '1', '0', d)}"
export USE_WAYLAND

# if using Wayland, let's use pure Wayland (and not XWayland)
# so do not build using X11 flag when we detect Wayland
USE_X11 = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland',  '0', \
       bb.utils.contains('DISTRO_FEATURES', 'x11',      '1', \
                                                        '0', d), d)}"
export USE_X11

# Use OpenGL ES 2.0 library
KIVY_GRAPHICS = "gles"
export KIVY_GRAPHICS

KIVY_CROSS_SYSROOT="${RECIPE_SYSROOT}"
export KIVY_CROSS_SYSROOT

REQUIRED_DISTRO_FEATURES += "opengl gobject-introspection-data"

ANY_OF_DISTRO_FEATURES = "x11 wayland"

DEPENDS += " \
    gstreamer1.0 \
    gstreamer1.0-python \
    libsdl2 \
    libsdl2-ttf \
    libsdl2-image \
    libsdl2-mixer \
    pango \
    python3 \
    python3-cython-native \
"

RDEPENDS:${PN} = " \
    gstreamer1.0 \
    gstreamer1.0-python \
    libsdl2 \
    libsdl2-ttf \
    libsdl2-image \
    libsdl2-mixer \
    pango \
    python3 \
    python3-docutils \
    python3-fcntl \
    python3-image \
    python3-pillow \
    python3-pygments \
"
