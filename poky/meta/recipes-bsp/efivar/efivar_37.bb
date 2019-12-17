SUMMARY = "Tools to manipulate UEFI variables"
DESCRIPTION = "efivar provides a simple command line interface to the UEFI variable facility"
HOMEPAGE = "https://github.com/rhboot/efivar"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6626bb1e20189cfa95f2c508ba286393"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRC_URI = "git://github.com/rhinstaller/efivar.git \
           file://no-werror.patch"
SRCREV = "c1d6b10e1ed4ba2be07f385eae5bceb694478a10"

S = "${WORKDIR}/git"

inherit pkgconfig

export CCLD_FOR_BUILD = "${BUILD_CCLD}"

# Upstream uses --add-needed in gcc.specs which gold doesn't support, so
# enforce BFD.
LDFLAGS += "-fuse-ld=bfd"

do_compile_prepend() {
    # Remove when https://github.com/rhboot/efivar/issues/130 is fixed
    oe_runmake \
        CFLAGS="${BUILD_CFLAGS}" \
        LDFLAGS="${BUILD_LDFLAGS}" \
        -C src makeguids
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native"

RRECOMMENDS_${PN}_class-target = "kernel-module-efivarfs"

CLEANBROKEN = "1"
