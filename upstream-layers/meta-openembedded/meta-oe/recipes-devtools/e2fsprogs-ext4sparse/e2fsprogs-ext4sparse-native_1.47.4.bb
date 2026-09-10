SUMMARY = "Native ext sparse conversion utility from e2fsprogs"
DESCRIPTION = "Builds only ext2simg for host-side ext* sparse image conversion"
HOMEPAGE = "http://e2fsprogs.sourceforge.net/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://contrib/android/ext2simg.c;beginline=1;endline=14;md5=fc8a826484cc82548adeaad2579adfca"

DEPENDS = "e2fsprogs-native android-tools-native"

SRC_URI = "git://git.kernel.org/pub/scm/fs/ext2/e2fsprogs.git;branch=master;protocol=https"
SRCREV = "ece89fac4603e400155b7bbf6326284f8511bca9"

inherit native

do_compile() {
    bbnote "Compiling ext2simg.c with native toolchain"
    # Source directory for ext2simg.c in the e2fsprogs tree
    SRC_EXT2SIMG="${S}/contrib/android"

    INCLUDES="-I${S}/lib -I${B}/lib -I${SRC_EXT2SIMG}/lib"

    # STAGING_LIBDIR_NATIVE: where android-tools-native installed libsparse
    LIBS="-L${B}/lib -L${RECIPE_SYSROOT_NATIVE}${libdir} -L${RECIPE_SYSROOT_NATIVE}${libdir}/android -lsparse -lext2fs -lz -lcom_err"

    ${CC} ${CFLAGS} ${LDFLAGS} -o "${B}/ext2simg_android" "${SRC_EXT2SIMG}/ext2simg.c" ${INCLUDES} ${LIBS}
    bbnote "ext2simg_android compilation finished"
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/ext2simg_android ${D}${bindir}/ext2simg_android
}

