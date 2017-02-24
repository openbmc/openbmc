SUMMARY = "Tools to manipulate UEFI variables"
DESCRIPTION = "efivar provides a simple command line interface to the UEFI variable facility"
HOMEPAGE = "https://github.com/rhinstaller/efivar"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6626bb1e20189cfa95f2c508ba286393"

DEPENDS = "popt"
DEPENDS_append_class-target = " efivar-native"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRCREV = "963cb2e5adc145fe00717f94e382f2973f80e753"
SRC_URI = "git://github.com/rhinstaller/efivar.git \
           file://0002-disable-static-build.patch \
"
SRC_URI_append_class-target = " file://0001-efivar-fix-for-cross-compile.patch \
                                file://0003-efivar-fix-for-cross-compile.patch \
                                ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'file://0004-fix-unknow-option-for-gold-linker.patch', '', d)} \
                              "
SRC_URI_append_class-native = " file://fix-compile-failure-with-host-gcc-4.6.patch \
                                file://allow-multi-definitions-for-native.patch \
                              "

S = "${WORKDIR}/git"

# Setting CROSS_COMPILE breaks pkgconfig, so just set AR
EXTRA_OEMAKE = "AR=${TARGET_PREFIX}gcc-ar"

do_compile_prepend() {
    sed -i -e s:-Werror::g ${S}/gcc.specs
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

do_install_append_class-native() {
    install -D -m 0755 ${B}/src/makeguids ${D}${bindir}/makeguids
}

BBCLASSEXTEND = "native"

