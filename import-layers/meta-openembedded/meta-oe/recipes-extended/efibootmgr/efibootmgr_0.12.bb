DESCRIPTION = "Linux user-space application to modify the EFI Boot Manager."
SUMMARY = "EFI Boot Manager"
HOMEPAGE = "https://github.com/rhinstaller/efibootmgr"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "pciutils zlib efivar"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRCREV = "75d25807ba81cb724964c989012611272c8f1f5d"
SRC_URI = "git://github.com/rhinstaller/efibootmgr.git;protocol=https \
           file://0001-Remove-extra-const-keywords-gcc-7-gripes-about.patch \
          "

S = "${WORKDIR}/git"


inherit pkgconfig

EXTRA_OEMAKE = "'CC=${CC}' 'CFLAGS=${CFLAGS} -I${S}/src/include `pkg-config --cflags efivar` \
                 -DEFIBOOTMGR_VERSION=\"$(RELEASE_MAJOR).$(RELEASE_MINOR)\" '"

do_install () {
    install -D -p -m0755 src/efibootmgr ${D}/${sbindir}/efibootmgr
}
