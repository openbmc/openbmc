SUMMARY = "Linux Library for low speed I/O Communication"
HOMEPAGE = "https://github.com/intel-iot-devkit/mraa"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=91e7de50a8d3cf01057f318d72460acd"

SRCREV = "e15ce6fbc76148ba8835adc92196b0d0a3f245e7"
PV = "2.1.0+git${SRCPV}"

SRC_URI = "git://github.com/eclipse/${BPN}.git;protocol=http \
           file://0001-cmake-Use-a-regular-expression-to-match-x86-architec.patch \
           "

S = "${WORKDIR}/git"

# CMakeLists.txt checks the architecture, only x86 and ARM supported for now
COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"

inherit cmake distutils3-base

DEPENDS += "json-c"

EXTRA_OECMAKE_append = " -DINSTALLTOOLS:BOOL=ON -DFIRMATA=ON -DCMAKE_SKIP_RPATH=ON -DPYTHON2_LIBRARY=OFF \
                         -DPYTHON3_PACKAGES_PATH:PATH=${baselib}/python${PYTHON_BASEVERSION}/site-packages \
                         -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                         -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI} \
                       "

CFLAGS += "-fcommon"

# Prepend mraa-utils to make sure bindir ends up in there
PACKAGES =+ "${PN}-utils"

FILES_${PN}-doc += "${datadir}/mraa/examples/"

FILES_${PN}-utils = "${bindir}/"

# override this in local.conf to get needed bindings.
# BINDINGS_pn-mraa="python"
# will result in only the python bindings being built/packaged.
# Note: 'nodejs' is disabled by default because the bindings
# generation currently fails with nodejs (>v7.x).
BINDINGS ??= "python"

# nodejs isn't available for armv4/armv5 architectures
BINDINGS_armv4 ??= "python"
BINDINGS_armv5 ??= "python"

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

TOOLCHAIN = "gcc"
