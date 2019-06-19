DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

DEPENDS_class-native += "openssl-native keyutils-native"

PV = "1.0+git${SRCPV}"
SRCREV = "0267fa16990fd0ddcc89984a8e55b27d43e80167"
SRC_URI = "git://git.code.sf.net/p/linux-ima/ima-evm-utils"

# Documentation depends on asciidoc, which we do not have, so
# do not build documentation.
SRC_URI += "file://disable-doc-creation.patch"

# Workaround for upstream incompatibility with older Linux distros.
# Relevant for us when compiling ima-evm-utils-native.
SRC_URI += "file://evmctl.c-do-not-depend-on-xattr.h-with-IMA-defines.patch"

# Required for xargs with more than one path as argument (better for performance).
SRC_URI += "file://command-line-apply-operation-to-all-paths.patch"

SRC_URI += "\
    file://0001-ima-evm-utils-link-to-libcrypto-instead-of-OpenSSL.patch \
    file://0002-ima-evm-utils-replace-INCLUDES-with-AM_CPPFLAGS.patch \
    file://0003-ima-evm-utils-include-hash-info.gen-into-distributio.patch \
    file://0004-ima-evm-utils-update-.gitignore-files.patch \
"
S = "${WORKDIR}/git"

inherit pkgconfig autotools

EXTRA_OECONF_append_class-target = " --with-kernel-headers=${STAGING_KERNEL_BUILDDIR}"

# blkid is called by evmctl when creating evm checksums.
# This is less useful when signing files on the build host,
# so disable it when compiling on the host.
RDEPENDS_${PN}_append_class-target = " util-linux-blkid libcrypto attr libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
