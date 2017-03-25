SUMMARY = "OpenGL extension loading library"
DESCRIPTION = "The OpenGL Extension Wrangler Library (GLEW) is a cross-platform open-source C/C++ extension loading library."
HOMEPAGE = "http://glew.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=67586"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ac251558de685c6b9478d89be3149c2"

DEPENDS = "virtual/libx11 virtual/libgl libglu libxext libxi libxmu"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/glew/glew/${PV}/glew-${PV}.tgz \
           file://no-strip.patch"

SRC_URI[md5sum] = "2a2cd7c98f13854d2fcddae0d2b20411"
SRC_URI[sha256sum] = "c572c30a4e64689c342ba1624130ac98936d7af90c3103f9ce12b8a0c5736764"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/glew/files/glew"
UPSTREAM_CHECK_REGEX = "/glew/(?P<pver>(\d+[\.\-_]*)+)/"

inherit lib_package pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

# Override SYSTEM to avoid calling config.guess, we're cross-compiling.  Pass
# our CFLAGS via POPT as that's the optimisation variable and safely
# overwritten.
EXTRA_OEMAKE = "SYSTEM='linux' \
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
