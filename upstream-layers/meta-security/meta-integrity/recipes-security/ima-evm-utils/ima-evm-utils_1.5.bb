DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

DEPENDS:class-native += "openssl-native keyutils-native"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI = " \
    https://github.com/mimizohar/ima-evm-utils/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-Do-not-get-generation-using-ioctl-when-evm_portable-.patch \
"
SRC_URI[sha256sum] = "45f1caa3ad59ec59a1d6a74ea5df38c413488cd952ab62d98cf893c15e6f246d"

inherit pkgconfig autotools features_check

REQUIRED_DISTRO_FEATURES = "ima"
REQUIRED_DISTRO_FEATURES:class-native = ""

EXTRA_OECONF += "MANPAGE_DOCBOOK_XSL=0"
EXTRA_OECONF:append:class-target = " --with-kernel-headers=${STAGING_KERNEL_BUILDDIR}"

# blkid is called by evmctl when creating evm checksums.
# This is less useful when signing files on the build host,
# so disable it when compiling on the host.
RDEPENDS:${PN}:append:class-target = " util-linux-blkid libcrypto attr libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
