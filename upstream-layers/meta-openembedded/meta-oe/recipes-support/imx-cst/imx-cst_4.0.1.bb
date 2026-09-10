SUMMARY = "i.MX code signing tool"
DESCRIPTION = "Code signing support that integrates the HABv4 and AHAB library for i.MX processors"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "\
    file://licenses/LICENSE.bsd3;md5=1ef4297097d818a9787ed775218c133f \
"

DEPENDS = "bison-native flex-native json-c openssl"

DEBIAN_PKG_NAME = "imx-code-signing-tool"
DEBIAN_PKG_VERSION = "${PV}+dfsg"

SRC_URI = "\
    ${DEBIAN_MIRROR}/main/i/${DEBIAN_PKG_NAME}/${DEBIAN_PKG_NAME}_${DEBIAN_PKG_VERSION}.orig.tar.xz \
    file://0001-check-return-value-of-fgets.patch \
    file://0002-use-ntohl-for-big-endian-compatibility.patch \
    file://0003-link-libjson-c-dynamically.patch \
    file://0004-fix-format-errors-on-32-bit-targets.patch \
    file://0005-fix-add-key-message-digest.patch \
    file://0006-unbreak-pki-scripts.patch \
    file://0007-fix-shell-syntax.patch \
    file://0008-fix-scripts-shebang.patch \
    file://0009-load-pkcs11-engine-dynamically.patch \
    file://0010-link-libhidapi-libusb-dynamically.patch \
    file://0011-link-libusb-dynamically.patch \
    file://0012-fix-openssl-4-asn1-opaque.patch \
    file://0013-convlb-remove-redundant-NULL-definition.patch \
    file://0014-fix-pointer-sign-errors-with-clang.patch \
"
SRC_URI[sha256sum] = "fd92a1a9faa10fb81bbf752c7ee1e257f17e1ec4c2964f8a47adf8a3eaa7df41"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/i/${DEBIAN_PKG_NAME}/"
UPSTREAM_CHECK_REGEX = "${DEBIAN_PKG_NAME}_(?P<pver>\d+(\.\d+)+)\+dfsg\d*\.orig\.tar\.xz"

S = "${UNPACKDIR}/${DEBIAN_PKG_NAME}-${DEBIAN_PKG_VERSION}"

OECMAKE_SOURCEPATH = "${S}/src"

# CST_INSTALL only controls installation of internal static libraries, not tools.
EXTRA_OECMAKE = "\
    -DCST_INSTALL=OFF \
    -DFLEX_TARGET_ARG_COMPILE_FLAGS=--noline \
    -DBISON_TARGET_ARG_COMPILE_FLAGS=--no-lines \
    -DJSONC_INCLUDE_DIR=${STAGING_INCDIR} \
"

inherit cmake

BBCLASSEXTEND = "native nativesdk"
