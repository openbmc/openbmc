SUMMARY = "OpenGL extension loading library"
DESCRIPTION = "The OpenGL Extension Wrangler Library (GLEW) is a cross-platform open-source C/C++ extension loading library."
HOMEPAGE = "http://glew.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=67586"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ac251558de685c6b9478d89be3149c2"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/glew/glew/${PV}/glew-${PV}.tgz \
           file://no-strip.patch"

SRC_URI[md5sum] = "b2ab12331033ddfaa50dc39345343980"
SRC_URI[sha256sum] = "04de91e7e6763039bc11940095cd9c7f880baba82196a7765f727ac05a993c95"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/glew/files/glew"
UPSTREAM_CHECK_REGEX = "/glew/(?P<pver>(\d+[\.\-_]*)+)/"

inherit lib_package pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'opengl', 'egl-gles2', d)}"

# The opengl and egl-XXX options are exclusive, enable only one.
PACKAGECONFIG[opengl] = "SYSTEM='linux',,virtual/libx11 virtual/libgl libglu libxext libxi libxmu"
PACKAGECONFIG[egl-gles2] = "SYSTEM='linux-egl' GLEW_NO_GLU='-DGLEW_NO_GLU' LDFLAGS.GL='-lEGL -lGLESv2',,virtual/egl virtual/libgles2"

CFLAGS += "-D_GNU_SOURCE"
# Override SYSTEM (via PACKAGECONFIG_CONFARGS) to avoid calling config.guess,
# we're cross-compiling. Pass our CFLAGS via POPT as that's the optimisation
# variable and safely overwritten.
EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS} \
                CC='${CC}' LD='${CC}' STRIP='' \
                LDFLAGS.EXTRA='${LDFLAGS}' \
                POPT='${CFLAGS}' \
                GLEW_PREFIX='${prefix}' BINDIR='${bindir}' \
                LIBDIR='${libdir}' INCDIR='${includedir}/GL' PKGDIR='${libdir}/pkgconfig'"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install.all
}
