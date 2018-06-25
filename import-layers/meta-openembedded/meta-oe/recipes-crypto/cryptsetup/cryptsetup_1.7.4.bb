SUMMARY = "Manage plain dm-crypt and LUKS encrypted volumes"
DESCRIPTION = "Cryptsetup is used to conveniently setup dm-crypt managed \
device-mapper mappings. These include plain dm-crypt volumes and \
LUKS volumes. The difference is that LUKS uses a metadata header \
and can hence offer more features than plain dm-crypt. On the other \
hand, the header is visible and vulnerable to damage."
HOMEPAGE = "http://code.google.com/p/cryptsetup/"
SECTION = "console"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=32107dd283b1dfeb66c9b3e6be312326"

DEPENDS = "util-linux libdevmapper popt libgcrypt"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/v1.7/${BP}.tar.xz"
SRC_URI[md5sum] = "058ed35c5b776032c2a036838f9e22a7"
SRC_URI[sha256sum] = "7ccf893ef79a38fb92d61f03c17b964982119f5319cdaa85a1335b8558cca016"

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

BBCLASSEXTEND = "native nativesdk"
