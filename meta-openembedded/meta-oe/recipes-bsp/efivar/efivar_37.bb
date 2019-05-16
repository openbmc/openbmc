SUMMARY = "Tools to manipulate UEFI variables"
DESCRIPTION = "efivar provides a simple command line interface to the UEFI variable facility"
HOMEPAGE = "https://github.com/rhinstaller/efivar"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6626bb1e20189cfa95f2c508ba286393"

DEPENDS = "popt"
DEPENDS_append_class-target = " efivar-native"

inherit pkgconfig

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRCREV = "c1d6b10e1ed4ba2be07f385eae5bceb694478a10"
SRC_URI = "git://github.com/rhinstaller/efivar.git \
           file://allow-multi-definitions-for-native.patch \
           "
SRC_URI_append_class-target = " file://0001-efivar-fix-for-cross-compile.patch \
                                ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'file://0004-fix-unknow-option-for-gold-linker.patch', '', d)} \
                              "

S = "${WORKDIR}/git"

do_compile_prepend() {
    sed -i -e s:-Werror::g ${S}/gcc.specs
}

do_compile_class-native() {
    oe_runmake -C src makeguids CC_FOR_BUILD="${BUILD_CC}"
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

do_install_class-native() {
    install -D -m 0755 ${B}/src/makeguids ${D}${bindir}/makeguids
}

BBCLASSEXTEND = "native"

RRECOMMENDS_${PN}_class-target = "kernel-module-efivarfs"
