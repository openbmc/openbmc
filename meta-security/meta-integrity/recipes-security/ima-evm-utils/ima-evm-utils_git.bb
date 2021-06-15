DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

DEPENDS_class-native += "openssl-native keyutils-native"

PV = "1.2.1+git${SRCPV}"
SRCREV = "3eab1f93b634249c1720f65fcb495b1996f0256e"
SRC_URI = "git://git.code.sf.net/p/linux-ima/ima-evm-utils;branch=ima-evm-utils-1.2.y"

# Documentation depends on asciidoc, which we do not have, so
# do not build documentation.
SRC_URI += "file://disable-doc-creation.patch"

# Workaround for upstream incompatibility with older Linux distros.
# Relevant for us when compiling ima-evm-utils-native.
SRC_URI += "file://evmctl.c-do-not-depend-on-xattr.h-with-IMA-defines.patch"

# Required for xargs with more than one path as argument (better for performance).
SRC_URI += "file://command-line-apply-operation-to-all-paths.patch"

S = "${WORKDIR}/git"

inherit pkgconfig autotools features_check

REQUIRED_DISTRO_FEATURES = "ima"
REQUIRED_DISTRO_FEATURES_class-native = ""

EXTRA_OECONF_append_class-target = " --with-kernel-headers=${STAGING_KERNEL_BUILDDIR}"

# blkid is called by evmctl when creating evm checksums.
# This is less useful when signing files on the build host,
# so disable it when compiling on the host.
RDEPENDS_${PN}_append_class-target = " util-linux-blkid libcrypto attr libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
