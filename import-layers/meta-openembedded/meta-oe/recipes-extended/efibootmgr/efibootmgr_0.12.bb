DESCRIPTION = "Linux user-space application to modify the EFI Boot Manager."
SUMMARY = "EFI Boot Manager"
HOMEPAGE = "https://github.com/rhinstaller/efibootmgr"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "pciutils zlib efivar"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRC_URI = "https://github.com/rhinstaller/efibootmgr/releases/download/${BP}/${BP}.tar.bz2 \
           file://ldflags.patch \
          "

SRC_URI[md5sum] = "6647f5cd807bc8484135ba74fcbcc39a"
SRC_URI[sha256sum] = "a66f5850677e86255d93cb1cead04c3c48a823a2b864c579321f2a07f00256e6"

EXTRA_OEMAKE = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/src/include `pkg-config --cflags efivar` \
                 -DEFIBOOTMGR_VERSION=\"$(RELEASE_MAJOR).$(RELEASE_MINOR)\" '"

do_install () {
    install -D -p -m0755 src/efibootmgr/efibootmgr ${D}/${sbindir}/efibootmgr
}

inherit pkgconfig

PNBLACKLIST[efibootmgr] ?= "Depends on blacklisted efivar"
