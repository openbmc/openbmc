SUMMARY = "RenderDoc recipe providing renderdoccmd"
DESCRIPTION = "RenderDoc is a frame-capture based graphics debugger"
HOMEPAGE = "https://github.com/baldurk/renderdoc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=df7ea9e196efc7014c124747a0ef9772"

SRCREV = "a56af589d94dc851809fd5344d0ae441da70c1f2"
SRC_URI = "git://github.com/baldurk/${BPN}.git;protocol=http;branch=v1.x \
	   file://0001-renderdoc-use-xxd-instead-of-cross-compiling-shim-bi.patch \
	   file://0001-Remove-glslang-pool_allocator-setAllocator.patch \
"
S = "${WORKDIR}/git"

DEPENDS += "virtual/libx11 virtual/libgl libxcb xcb-util-keysyms vim-native"

RDEPENDS_${PN} = "libxcb xcb-util-keysyms"

inherit cmake python3native features_check

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
"

FILES_${PN} += "${libdir}"
FILES_${PN}-dev = "${includedir}"
