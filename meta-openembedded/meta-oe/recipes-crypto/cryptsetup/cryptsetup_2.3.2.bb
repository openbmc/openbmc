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

DEPENDS = " \
    json-c \
    libdevmapper \
    popt \
    util-linux \
"

RDEPENDS_${PN} = " \
    libdevmapper \
"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/v${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}/${BP}.tar.xz"
SRC_URI[md5sum] = "6e4ffb6d35a73f7539a5d0c1354654cd"
SRC_URI[sha256sum] = "a89e13dff0798fd0280e801d5f0cc8cfdb2aa5b1929bec1b7322e13d3eca95fb"

inherit autotools gettext pkgconfig

# Use openssl because libgcrypt drops root privileges
# if libgcrypt is linked with libcap support
PACKAGECONFIG ??= " \
    keyring \
    cryptsetup \
    veritysetup \
    cryptsetup-reencrypt \
    integritysetup \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
    kernel_crypto \
    internal-argon2 \
    blkid \
    luks-adjust-xts-keysize \
    openssl \
"
PACKAGECONFIG_append_class-target = " \
    udev \
"

PACKAGECONFIG[keyring] = "--enable-keyring,--disable-keyring"
PACKAGECONFIG[fips] = "--enable-fips,--disable-fips"
PACKAGECONFIG[pwquality] = "--enable-pwquality,--disable-pwquality,libpwquality"
PACKAGECONFIG[passwdqc] = "--enable-passwdqc,--disable-passwdqc,passwdqc"
PACKAGECONFIG[cryptsetup] = "--enable-cryptsetup,--disable-cryptsetup"
PACKAGECONFIG[veritysetup] = "--enable-veritysetup,--disable-veritysetup"
PACKAGECONFIG[cryptsetup-reencrypt] = "--enable-cryptsetup-reencrypt,--disable-cryptsetup-reencrypt"
PACKAGECONFIG[integritysetup] = "--enable-integritysetup,--disable-integritysetup"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,,udev lvm2-udevrules"
PACKAGECONFIG[kernel_crypto] = "--enable-kernel_crypto,--disable-kernel_crypto"
# gcrypt-pkbdf2 requries --with-crypto_backend=gcrypt or the flag isn't
# recognized.
PACKAGECONFIG[gcrypt-pbkdf2] = "--enable-gcrypt-pbkdf2"
PACKAGECONFIG[internal-argon2] = "--enable-internal-argon2,--disable-internal-argon2"
PACKAGECONFIG[internal-sse-argon2] = "--enable-internal-sse-argon2,--disable-internal-sse-argon2"
PACKAGECONFIG[blkid] = "--enable-blkid,--disable-blkid,util-linux"
PACKAGECONFIG[dev-random] = "--enable-dev-random,--disable-dev-random"
PACKAGECONFIG[luks-adjust-xts-keysize] = "--enable-luks-adjust-xts-keysize,--disable-luks-adjust-xts-keysize"
PACKAGECONFIG[openssl] = "--with-crypto_backend=openssl,,openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto_backend=gcrypt,,libgcrypt"
PACKAGECONFIG[nss] = "--with-crypto_backend=nss,,nss"
PACKAGECONFIG[kernel] = "--with-crypto_backend=kernel"
PACKAGECONFIG[nettle] = "--with-crypto_backend=nettle,,nettle"
PACKAGECONFIG[luks2] = "--with-default-luks-format=LUKS2,--with-default-luks-format=LUKS1"

RRECOMMENDS_${PN} = "kernel-module-aes-generic \
                     kernel-module-dm-crypt \
                     kernel-module-md5 \
                     kernel-module-cbc \
                     kernel-module-sha256-generic \
                     kernel-module-xts \
"

EXTRA_OECONF = "--enable-static"
# Building without largefile is not supported by upstream
EXTRA_OECONF += "--enable-largefile"
# Requires a static popt library
EXTRA_OECONF += "--disable-static-cryptsetup"
# There's no recipe for libargon2 yet
EXTRA_OECONF += "--disable-libargon2"

FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','${exec_prefix}/lib/tmpfiles.d/cryptsetup.conf', '', d)}"

BBCLASSEXTEND = "native nativesdk"
