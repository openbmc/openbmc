SUMMARY = "OpenGL driver testing framework"
DESCRIPTION = "Piglit is an open-source test suite for OpenGL and OpenCL \
implementations."
LICENSE = "MIT & LGPLv2+ & GPLv3 & GPLv2+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b2beded7103a3d8a442a2a0391d607b0"

SRC_URI = "git://gitlab.freedesktop.org/mesa/piglit.git;protocol=https \
           file://0001-cmake-install-bash-completions-in-the-right-place.patch \
           file://0001-cmake-use-proper-WAYLAND_INCLUDE_DIRS-variable.patch \
           "
UPSTREAM_CHECK_COMMITS = "1"

SRCREV = "6126c2d4e476c7770d216ffa1932c10e2a5a7813"
# (when PV goes above 1.0 remove the trailing r)
PV = "1.0+gitr${SRCPV}"

S = "${WORKDIR}/git"

X11_DEPS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11 libxrender libglu', '', d)}"
X11_RDEPS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'mesa-demos', '', d)}"

DEPENDS = "libpng waffle libxkbcommon virtual/libgl python3-mako-native python3-numpy-native python3-six-native virtual/egl"

inherit cmake pkgconfig python3native features_check bash-completion

# depends on virtual/libgl
REQUIRED_DISTRO_FEATURES += "opengl"

# The built scripts go into the temporary directory according to tempfile
# (typically /tmp) which can race if multiple builds happen on the same machine,
# so tell it to use a directory in ${B} to avoid overwriting.
export TEMP = "${B}/temp/"
do_compile[dirs] =+ "${B}/temp/"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[freeglut] = "-DPIGLIT_USE_GLUT=1,-DPIGLIT_USE_GLUT=0,freeglut,"
PACKAGECONFIG[x11] = "-DPIGLIT_BUILD_GL_TESTS=ON,-DPIGLIT_BUILD_GL_TESTS=OFF,${X11_DEPS}, ${X11_RDEPS}"


do_configure_prepend() {
   if [ "${@bb.utils.contains('PACKAGECONFIG', 'freeglut', 'yes', 'no', d)}" = "no" ]; then
        sed -i -e "/^#.*include <GL\/freeglut_ext.h>$/d" ${S}/src/piglit/glut_wrap.h
        sed -i -e "/^#.*include.*<GL\/glut.h>$/d" ${S}/src/piglit/glut_wrap.h
   fi
}

# Forcibly strip because Piglit is *huge*
OECMAKE_TARGET_INSTALL = "install/strip"

RDEPENDS_${PN} = "waffle waffle-bin python3 python3-mako python3-json \
	python3-misc \
	python3-unixadmin python3-xml python3-multiprocessing \
	python3-six python3-shell python3-io \
	python3-netserver bash \
	"

INSANE_SKIP_${PN} += "dev-so already-stripped"

# As nothing builds against Piglit we don't need to have anything in the
# sysroot, especially when this is ~2GB of test suite
SYSROOT_DIRS_remove = "${libdir}"

# Can't be built with ccache
CCACHE_DISABLE = "1"
