SUMMARY = "Tools to manipulate UEFI variables"
DESCRIPTION = "efivar provides a simple command line interface to the UEFI variable facility"
HOMEPAGE = "https://github.com/rhboot/efivar"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6626bb1e20189cfa95f2c508ba286393"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

SRC_URI = "git://github.com/rhinstaller/efivar.git;branch=main;protocol=https \
           file://0001-docs-do-not-build-efisecdb-manpage.patch \
           file://0001-src-Makefile-build-util.c-separately-for-makeguids.patch \
           file://efisecdb-fix-build-with-musl-libc.patch \
           file://0001-Fix-invalid-free-in-main.patch \
           file://0001-Fix-glibc-2.36-build-mount.h-conflicts.patch \
           file://0001-Use-off_t-instead-of-off64_t.patch \
           "
SRCREV = "1753149d4176ebfb2b135ac0aaf79340bf0e7a93"

S = "${WORKDIR}/git"

inherit pkgconfig

export CCLD_FOR_BUILD = "${BUILD_CCLD}"

# Upstream uses --add-needed in gcc.specs which gold doesn't support, so
# enforce BFD.
LDFLAGS += "-fuse-ld=bfd"

do_compile() {
    oe_runmake ERRORS= HOST_CFLAGS="${BUILD_CFLAGS}" HOST_LDFLAGS="${BUILD_LDFLAGS}"
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native"

RRECOMMENDS:${PN}:class-target = "kernel-module-efivarfs"

CLEANBROKEN = "1"
