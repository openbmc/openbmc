SUMMARY = "Manage plain dm-crypt and LUKS encrypted volumes"
DESCRIPTION = "Cryptsetup is used to conveniently setup dm-crypt managed \
device-mapper mappings. These include plain dm-crypt volumes and \
LUKS volumes. The difference is that LUKS uses a metadata header \
and can hence offer more features than plain dm-crypt. On the other \
hand, the header is visible and vulnerable to damage."
HOMEPAGE = "https://gitlab.com/cryptsetup/cryptsetup"
SECTION = "console"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=32107dd283b1dfeb66c9b3e6be312326"

DEPENDS = "util-linux libdevmapper popt libgcrypt json-c"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/v2.0/${BP}.tar.xz"
SRC_URI[md5sum] = "ed42b31f67d05b05e392d1943d467b8d"
SRC_URI[sha256sum] = "9d3a3c7033293e0c97f0ad0501fd5b4d4913ae497cbf70cca06633ccc54b5734"

inherit autotools gettext pkgconfig

# Use openssl because libgcrypt drops root privileges
# if libgcrypt is linked with libcap support
PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto_backend=openssl,,openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto_backend=gcrypt,,libgcrypt"

RRECOMMENDS_${PN} = "kernel-module-aes-generic \
                     kernel-module-dm-crypt \
                     kernel-module-md5 \
                     kernel-module-cbc \
                     kernel-module-sha256-generic \
                     kernel-module-xts \
"

EXTRA_OECONF = "--enable-static"

FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${exec_prefix}/lib/tmpfiles.d/cryptsetup.conf', '', d)}"

BBCLASSEXTEND = "native nativesdk"
