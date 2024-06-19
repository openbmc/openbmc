SUMMARY = "FIDO 2.0 support library"
DESCRIPTION = "libfido2 provides library functionality and command-line tools to \
communicate with a FIDO device over USB, and to verify attestation and \
assertion signatures."
HOMEPAGE = "https://developers.yubico.com/libfido2"
LICENSE = "BSD-2-Clause"
SECTION = "libs/network"
DEPENDS = "libcbor openssl zlib udev"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5f14cb32bdf2b87063e0a2d20c4178d0"

SRC_URI = "https://developers.yubico.com/${BPN}/Releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "3601792e320032d428002c4cce8499a4c7b803319051a25a0c9f1f138ffee45a"

inherit cmake pkgconfig manpages

PACKAGECONFIG[manpages] = "-DBUILD_MANPAGES:BOOL=ON,-DBUILD_MANPAGES:BOOL=OFF"

EXTRA_OECMAKE = "-DUDEV_RULES_DIR=${nonarch_base_libdir}/udev/rules.d -DBUILD_EXAMPLES:BOOL=OFF"

PACKAGE_BEFORE_PN = "${PN}-tools"

FILES:${PN}-tools = "${bindir}/fido2-*"
