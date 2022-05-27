DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

DEPENDS:class-native += "openssl-native keyutils-native"

SRC_URI = "https://sourceforge.net/projects/linux-ima/files/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "fcf85b31d6292051b3679e5f17ffa7f89b6898957aad0f59aa4e9878884b27d1"

inherit pkgconfig autotools features_check

REQUIRED_DISTRO_FEATURES = "ima"
REQUIRED_DISTRO_FEATURES:class-native = ""

EXTRA_OECONF:append:class-target = " --with-kernel-headers=${STAGING_KERNEL_BUILDDIR}"

# blkid is called by evmctl when creating evm checksums.
# This is less useful when signing files on the build host,
# so disable it when compiling on the host.
RDEPENDS:${PN}:append:class-target = " util-linux-blkid libcrypto attr libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
