SUMMARY = "Linux Library for low speed I/O Communication"
HOMEPAGE = "https://github.com/intel-iot-devkit/mraa"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=66493d54e65bfc12c7983ff2e884f37f"

SRCREV = "8ddbcde84e2d146bc0f9e38504d6c89c14291480"
PV = "1.7.0-git${SRCPV}"

SRC_URI = "git://github.com/intel-iot-devkit/${BPN}.git;protocol=http"

S = "${WORKDIR}/git"

# CMakeLists.txt checks the architecture, only x86 and ARM supported for now
COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"

inherit cmake distutils3-base

DEPENDS += "json-c"

EXTRA_OECMAKE_append = " -DINSTALLTOOLS:BOOL=ON -DFIRMATA=ON -DCMAKE_SKIP_RPATH=ON"

# Prepend mraa-utils to make sure bindir ends up in there
PACKAGES =+ "${PN}-utils"

FILES_${PN}-doc += "${datadir}/mraa/examples/"

FILES_${PN}-utils = "${bindir}/"

# override this in local.conf to get needed bindings.
# BINDINGS_pn-mraa="python"
# will result in only the python bindings being built/packaged.
BINDINGS ??= "python ${@ 'nodejs' if oe.types.boolean(d.getVar('HAVE_NODEJS') or '0') else '' }"

PACKAGECONFIG ??= "${@bb.utils.contains('PACKAGES', 'node-${PN}', 'nodejs', '', d)} \
 ${@bb.utils.contains('PACKAGES', '${PYTHON_PN}-${PN}', 'python', '', d)}"

PACKAGECONFIG[python] = "-DBUILDSWIGPYTHON=ON, -DBUILDSWIGPYTHON=OFF, swig-native ${PYTHON_PN},"
PACKAGECONFIG[nodejs] = "-DBUILDSWIGNODE=ON, -DBUILDSWIGNODE=OFF, swig-native nodejs-native,"
PACKAGECONFIG[ft4222] = "-DUSBPLAT=ON -DFTDI4222=ON, -DUSBPLAT=OFF -DFTDI4222=OFF,, libft4222"

FILES_${PYTHON_PN}-${PN} = "${PYTHON_SITEPACKAGES_DIR}/"
RDEPENDS_${PYTHON_PN}-${PN} += "${PYTHON_PN}"

FILES_node-${PN} = "${prefix}/lib/node_modules/"
RDEPENDS_node-${PN} += "nodejs"

### Include desired language bindings ###
PACKAGES =+ "${@bb.utils.contains('BINDINGS', 'nodejs', 'node-${PN}', '', d)}"
PACKAGES =+ "${@bb.utils.contains('BINDINGS', 'python', '${PYTHON_PN}-${PN}', '', d)}"
