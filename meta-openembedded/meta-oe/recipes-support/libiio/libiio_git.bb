SUMMARY = "Library for interfacing with IIO devices"
HOMEPAGE = "https://wiki.analog.com/resources/tools-software/linux-software/libiio"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=7c13b3376cea0ce68d2d2da0a1b3a72c"

SRCREV = "565bf68eccfdbbf22cf5cb6d792e23de564665c7"
PV = "0.21+git${SRCPV}"

SRC_URI = "git://github.com/analogdevicesinc/libiio.git;protocol=https;branch=master \
           file://0001-python-Do-not-verify-whether-libiio-is-installed-whe.patch \
"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

DISTUTILS_SETUP_PATH ?= "${B}/bindings/python/"

DEPENDS = " \
    flex-native bison-native libaio \
"

inherit cmake python3native systemd setuptools3

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DUDEV_RULES_INSTALL_DIR=${nonarch_base_libdir}/udev/rules.d \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-DWITH_SYSTEMD=ON -DSYSTEMD_UNIT_INSTALL_DIR=${systemd_system_unitdir}', '', d)} \
"

PACKAGECONFIG ??= "usb_backend network_backend serial_backend"

NETWORK_BACKEND_DEPENDENCIES = "\
    libxml2 \
    ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
"

PACKAGECONFIG[usb_backend] = "-DWITH_USB_BACKEND=ON,-DWITH_USB_BACKEND=OFF,libusb1 libxml2"
PACKAGECONFIG[network_backend] = "-DWITH_NETWORK_BACKEND=ON,-DWITH_NETWORK_BACKEND=OFF, ${NETWORK_BACKEND_DEPENDENCIES}"
PACKAGECONFIG[serial_backend] = "-DWITH_SERIAL_BACKEND=ON,-DWITH_SERIAL_BACKEND=off,libserialport libxml2"
PACKAGECONFIG[libiio-python3] = "-DPYTHON_BINDINGS=ON,-DPYTHON_BINDINGS=OFF"

PACKAGES =+ "${PN}-iiod ${PN}-tests ${PN}-${PYTHON_PN}"

# Inheriting setuptools3 incorrectly adds the dependency on ${PYTHON_PN}-core
# to ${PN} instead of to ${PN}-${PYTHON_PN} where it belongs.
RDEPENDS:${PN}:remove = "${PYTHON_PN}-core"
RDEPENDS:${PN}-${PYTHON_PN} = "${PN} ${PYTHON_PN}-core ${PYTHON_PN}-ctypes ${PYTHON_PN}-stringold"

FILES:${PN}-iiod = " \
    ${sbindir}/iiod \
    ${systemd_system_unitdir}/iiod.service \
"
FILES:${PN}-tests = "${bindir}"
FILES:${PN}-${PYTHON_PN} = "${PYTHON_SITEPACKAGES_DIR}"

SYSTEMD_PACKAGES = "${PN}-iiod"
SYSTEMD_SERVICE:${PN}-iiod = "iiod.service"

# Explicitly define do_configure, do_compile and do_install because both cmake and setuptools3 have
# EXPORT_FUNCTIONS do_configure do_compile do_install
do_configure() {
    cmake_do_configure
}

do_compile() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'libiio-python3', 'true', 'false', d)}; then
        distutils3_do_compile
    fi
    cmake_do_compile
}

do_install() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'libiio-python3', 'true', 'false', d)}; then
        distutils3_do_install
    fi
    cmake_do_install
}
