require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6dc95e63aa4d72502ff8193dfe2ddd38"

SRC_URI += "\
    file://not-check-libperl.patch \
"

do_compile_prepend_libc-musl() {
    sed -i -e 's/\-lnsl//g' ${B}/src/Makefile.global
}

SRC_URI[md5sum] = "0a08f4078f5e4a54e764f63ad38a6de3"
SRC_URI[sha256sum] = "7a320cd335052b840d209dc9688f09965763351c590e3cc7bf577591179fd7c6"
