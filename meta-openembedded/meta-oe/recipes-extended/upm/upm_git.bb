SUMMARY = "Sensor/Actuator repository for Mraa"
HOMEPAGE = "https://github.com/intel-iot-devkit/upm"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66493d54e65bfc12c7983ff2e884f37f"

DEPENDS = "libjpeg-turbo mraa"

SRCREV = "dc45cd78595c7c24c8a8574c63bb48b5bb99c5aa"
PV = "1.6.0-git${SRCPV}"

SRC_URI = "git://github.com/intel-iot-devkit/${BPN}.git;protocol=http \
           file://0001-Replace-strncpy-with-memcpy.patch \
           file://0001-include-sys-types.h-for-uint-definition.patch \
           "

S = "${WORKDIR}/git"

# Depends on mraa which only supports x86 and ARM for now
COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"

inherit distutils3-base cmake

# override this in local.conf to get needed bindings.
# BINDINGS_pn-upm="python"
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

FILES_${PYTHON_PN}-${PN} = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS_${PYTHON_PN}-${PN} += "${PYTHON_PN}"

FILES_node-${PN} = "${prefix}/lib/node_modules/"
RDEPENDS_node-${PN} += "nodejs"

### Include desired language bindings ###
PACKAGES =+ "${@bb.utils.contains('BINDINGS', 'nodejs', 'node-${PN}', '', d)}"
PACKAGES =+ "${@bb.utils.contains('BINDINGS', 'python', '${PYTHON_PN}-${PN}', '', d)}"
