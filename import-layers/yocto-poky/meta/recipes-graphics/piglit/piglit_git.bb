SUMMARY = "OpenGL driver testing framework"
LICENSE = "MIT & LGPLv2+ & GPLv3 & GPLv2+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b2beded7103a3d8a442a2a0391d607b0"

SRC_URI = "git://anongit.freedesktop.org/piglit \
           file://0001-tests-Fix-missing-include-of-Xutil.h.patch"

# From 2015-02-19
SRCREV = "c4585427913e4cb28994b4bfb11d49778273aa2c"
# (when PV goes above 1.0 remove the trailing r)
PV = "1.0+gitr${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "virtual/libx11 libxrender waffle virtual/libgl libglu python-mako-native python-numpy-native"

inherit cmake pythonnative distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

# The built scripts go into the temporary directory according to tempfile
# (typically /tmp) which can race if multiple builds happen on the same machine,
# so tell it to use a directory in ${B} to avoid overwriting.
export TEMP = "${B}/temp/"
do_compile[dirs] =+ "${B}/temp/"

PACKAGECONFIG ??= ""
PACKAGECONFIG[freeglut] = "-DPIGLIT_USE_GLUT=1,-DPIGLIT_USE_GLUT=0,freeglut,"

do_configure_prepend() {
   if [ "${@bb.utils.contains('PACKAGECONFIG', 'freeglut', 'yes', 'no', d)}" = "no" ]; then
        sed -i -e "/^#.*include <GL\/freeglut_ext.h>$/d" ${S}/src/piglit/glut_wrap.h
        sed -i -e "/^#.*include.*<GL\/glut.h>$/d" ${S}/src/piglit/glut_wrap.h
   fi
}

RDEPENDS_${PN} = "waffle python python-mako python-json python-subprocess \
	python-argparse python-importlib python-unixadmin python-xml \
	python-multiprocessing python-textutils python-netserver python-shell \
	mesa-demos bash \
	"

INSANE_SKIP_${PN} += "dev-so"
