DESCRIPTION = "Linux user-space application to modify the EFI Boot Manager."
SUMMARY = "EFI Boot Manager"
HOMEPAGE = "https://github.com/rhinstaller/efibootmgr"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "pciutils zlib efivar"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRCREV = "5c14da22802576a99ebb854f7aa174f796f7b031"
SRC_URI = "git://github.com/rhinstaller/efibootmgr.git;protocol=https \
          "
S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE = "'EFIDIR=/' 'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/src/include `pkg-config --cflags efivar`'"

CFLAGS_append_toolchain-clang = " -Wno-error"
do_install () {
    install -D -p -m0755 ${B}/src/efibootmgr ${D}/${sbindir}/efibootmgr
}

CLEANBROKEN = "1"
