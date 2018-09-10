require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6dc95e63aa4d72502ff8193dfe2ddd38"

SRC_URI += "\
    file://not-check-libperl.patch \
"

do_compile_prepend_libc-musl() {
    sed -i -e 's/\-lnsl//g' ${B}/src/Makefile.global
}

SRC_URI[md5sum] = "5821867741c821736266f27b6b8a859a"
SRC_URI[sha256sum] = "eeb1d8ddb2854c9e4d8b5cbd65665260c0ae8cbcb911003f24c2d82ccb97f87f"
