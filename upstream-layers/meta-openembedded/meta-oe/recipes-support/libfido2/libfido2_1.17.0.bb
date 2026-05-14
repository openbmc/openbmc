SUMMARY = "FIDO 2.0 support library"
DESCRIPTION = "libfido2 provides library functionality and command-line tools to \
communicate with a FIDO device over USB, and to verify attestation and \
assertion signatures."
HOMEPAGE = "https://developers.yubico.com/libfido2"
LICENSE = "BSD-2-Clause"
SECTION = "libs/network"
DEPENDS = "libcbor openssl zlib udev"

LIC_FILES_CHKSUM = "file://LICENSE;md5=81b68c65611c3a2eaac9e44814284719"

SRC_URI = "https://developers.yubico.com/${BPN}/Releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "c1012c8871d71b65872fd5ff1a9d6b0838a55683a03e85ba97479ce57129c736"

inherit cmake pkgconfig manpages

PACKAGECONFIG[manpages] = "-DBUILD_MANPAGES:BOOL=ON,-DBUILD_MANPAGES:BOOL=OFF"

EXTRA_OECMAKE = "-DUDEV_RULES_DIR=${nonarch_base_libdir}/udev/rules.d -DBUILD_EXAMPLES:BOOL=OFF"

PACKAGE_BEFORE_PN = "${PN}-tools"

FILES:${PN}-tools = "${bindir}/fido2-*"
