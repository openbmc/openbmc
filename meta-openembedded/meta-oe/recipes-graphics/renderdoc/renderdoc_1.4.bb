SUMMARY = "RenderDoc recipe providing renderdoccmd"
DESCRIPTION = "RenderDoc is a frame-capture based graphics debugger"
HOMEPAGE = "https://github.com/baldurk/renderdoc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9753b1b4fba3261c27d1ce5c1acef667"

SRCREV = "214d85228538e71cc63a0d7fa11dd75b1d56cc81"
SRC_URI = "git://github.com/baldurk/${BPN}.git;protocol=http;branch=v1.x \
	   file://0001-renderdoc-use-xxd-instead-of-cross-compiling-shim-bi.patch \
"
S = "${WORKDIR}/git"

DEPENDS += "virtual/libx11 virtual/libgl libxcb xcb-util-keysyms vim-native"

RDEPENDS_${PN} = "libxcb xcb-util-keysyms"

inherit cmake python3native distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

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
"

FILES_${PN} += "${libdir}"
FILES_${PN}-dev = "${includedir}"
