SUMMARY = "RenderDoc recipe providing renderdoccmd"
DESCRIPTION = "RenderDoc is a frame-capture based graphics debugger"
HOMEPAGE = "https://github.com/baldurk/renderdoc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=963d134bd809d24547253120513346d3"

SRCREV = "34c3c40787f440da9c2947cd63a41e6c4d1f95b9"
SRC_URI = " \
    git://github.com/baldurk/${BPN}.git;protocol=https;branch=v1.x \
"

DEPENDS += "virtual/libx11 virtual/libgl libxcb xcb-util-keysyms"

RDEPENDS:${PN} = "libxcb xcb-util-keysyms"

inherit cmake pkgconfig python3native features_check

REQUIRED_DISTRO_FEATURES = "x11 opengl"

python __anonymous () {
    # only works on glibc systems
    if d.getVar('TCLIBC') != "glibc":
        raise bb.parse.SkipRecipe("incompatible with %s C library" % d.getVar('TCLIBC'))
}

COMPATIBLE_HOST = "(x86_64|i.86|arm|aarch64).*-linux"

EXTRA_OECMAKE += "\
    -DENABLE_QRENDERDOC=OFF \
    -DENABLE_PYRENDERDOC=OFF \
    -DENABLE_RENDERDOCCMD=ON \
    -DCMAKE_BUILD_TYPE=Release \
    -DHOST_NATIVE_CPP_COMPILER="${BUILD_CXX}" \
"

FILES:${PN} += "${libdir}"
FILES:${PN}-dev = "${includedir}"
