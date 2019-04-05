SUMMARY = "An extension to cryptsetup/LUKS that enables use of the TPM 2.0 via tpm2-tss"
DESCRIPTION = "Cryptsetup is utility used to conveniently setup disk encryption based on DMCrypt kernel module."

SECTION = "security/tpm"
LICENSE = "LGPL-2.1 | GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=32107dd283b1dfeb66c9b3e6be312326 \
                    file://COPYING.LGPL;md5=1960515788100ce5f9c98ea78a65dc52 \
                    "

DEPENDS = "autoconf-archive pkgconfig gettext libtss2-dev libdevmapper popt libgcrypt json-c"

SRC_URI = "git://github.com/AndreasFuchsSIT/cryptsetup-tpm-incubator.git;branch=luks2tpm \
           file://configure_fix.patch "

SRCREV = "15c283195f19f1d980e39ba45448683d5e383179"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto_backend=openssl,,openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto_backend=gcrypt,,libgcrypt"

EXTRA_OECONF = "--enable-static"

RRECOMMENDS_${PN} = "kernel-module-aes-generic \
                     kernel-module-dm-crypt \
                     kernel-module-md5 \
                     kernel-module-cbc \
                     kernel-module-sha256-generic \
                     kernel-module-xts \
                    "

RDEPENDS_${PN} += "lvm2"
RRECOMMENDS_${PN} += "lvm2-udevrules"

RREPLACES_${PN} = "cryptsetup"
RCONFLICTS_${PN}  ="cryptsetup"

BBCLASSEXTEND = "native nativesdk"
