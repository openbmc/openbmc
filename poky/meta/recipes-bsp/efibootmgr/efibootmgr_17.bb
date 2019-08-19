DESCRIPTION = "Linux user-space application to modify the EFI Boot Manager."
SUMMARY = "EFI Boot Manager"
HOMEPAGE = "https://github.com/rhboot/efibootmgr"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "efivar popt"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRC_URI = "git://github.com/rhinstaller/efibootmgr.git;protocol=https \
           file://0001-remove-extra-decl.patch \
           file://97668ae0bce776a36ea2001dea63d376be8274ac.patch \
          "
SRCREV = "e067160ecef8208e1944002e5d50b275733211fb"

S = "${WORKDIR}/git"

inherit pkgconfig

# The directory under the ESP that the default bootloader is found in.  When
# wic uses a subdirectory, this should use the same one too.
EFIDIR ?= "/"

EXTRA_OEMAKE += "'EFIDIR=${EFIDIR}'"

CFLAGS += " -Wno-error"

do_install () {
	oe_runmake install DESTDIR="${D}"
}

CLEANBROKEN = "1"
