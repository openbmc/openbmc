SUMMARY = "Gummiboot is a simple UEFI boot manager which executes configured EFI images."
HOMEPAGE = "http://freedesktop.org/wiki/Software/gummiboot"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "gnu-efi util-linux"

inherit autotools pkgconfig
inherit deploy

PV = "48+git${SRCPV}"
SRCREV = "2bcd919c681c952eb867ef1bdb458f1bc49c2d55"
SRC_URI = "git://anongit.freedesktop.org/gummiboot \
           file://fix-objcopy.patch \
           file://0001-console-Fix-C-syntax-errors-for-function-declaration.patch \
          "

# Note: Add COMPATIBLE_HOST here is only because it depends on gnu-efi
# which has set the COMPATIBLE_HOST, the gummiboot itself may work on
# more hosts.
COMPATIBLE_HOST = "(x86_64.*|i.86.*)-linux"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--disable-manpages --with-efi-includedir=${STAGING_INCDIR} \
                --with-efi-ldsdir=${STAGING_LIBDIR} \
                --with-efi-libdir=${STAGING_LIBDIR}"

EXTRA_OEMAKE += "gummibootlibdir=${libdir}/gummiboot"

TUNE_CCARGS_remove = "-mfpmath=sse"

do_deploy () {
        install ${B}/gummiboot*.efi ${DEPLOYDIR}
}
addtask deploy before do_build after do_compile
