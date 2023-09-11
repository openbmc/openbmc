SUMMARY = "Library for interfacing with IIO devices"
HOMEPAGE = "https://wiki.analog.com/resources/tools-software/linux-software/libiio"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=7c13b3376cea0ce68d2d2da0a1b3a72c"

SRCREV = "b6028fdeef888ab45f7c1dd6e4ed9480ae4b55e3"

SRC_URI = "git://github.com/analogdevicesinc/libiio.git;protocol=https;branch=main"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

SETUPTOOLS_SETUP_PATH ?= "${B}/bindings/python/"

DEPENDS = " \
    flex-native bison-native libaio \
"

inherit cmake python3native systemd setuptools3 pkgconfig

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DCPP_BINDINGS=ON \
    -DFLEX_TARGET_ARG_COMPILE_FLAGS='--noline' \
    -DBISON_TARGET_ARG_COMPILE_FLAGS='--no-lines' \
    -DUDEV_RULES_INSTALL_DIR=${nonarch_base_libdir}/udev/rules.d \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-DWITH_SYSTEMD=ON -DSYSTEMD_UNIT_INSTALL_DIR=${systemd_system_unitdir}', '', d)} \
"

PACKAGECONFIG ??= " \
    usb_backend network_backend serial_backend xml_backend \
    ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'dnssd', '', d)} \
"

# network_backend, serial_backend and usb_backend depend on xml_backend, so don't switch it off
XML_BACKEND_DISABLE = "${@bb.utils.contains_any('PACKAGECONFIG', 'network_backend serial_backend usb_backend', '', '-DWITH_XML_BACKEND=off', d)}"

PACKAGECONFIG[usb_backend] = "-DWITH_USB_BACKEND=ON -DWITH_XML_BACKEND=ON,-DWITH_USB_BACKEND=OFF,libusb1 libxml2"
PACKAGECONFIG[network_backend] = "-DWITH_NETWORK_BACKEND=ON -DWITH_XML_BACKEND=ON,-DWITH_NETWORK_BACKEND=OFF,libxml2"
PACKAGECONFIG[serial_backend] = "-DWITH_SERIAL_BACKEND=ON -DWITH_XML_BACKEND=ON,-DWITH_SERIAL_BACKEND=off,libserialport libxml2"
PACKAGECONFIG[xml_backend] = "-DWITH_XML_BACKEND=ON,${XML_BACKEND_DISABLE},libxml2"
PACKAGECONFIG[dnssd] = "-DHAVE_DNS_SD=ON,-DHAVE_DNS_SD=off,avahi"
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
        setuptools3_do_compile
    fi
    cmake_do_compile
}

PIP_INSTALL_PACKAGE = "pylibiio"
do_install() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'libiio-python3', 'true', 'false', d)}; then
        setuptools3_do_install
    fi
    cmake_do_install
}
