require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=81b69ddb31a8be66baafd14a90146ee2"

SRC_URI += "\
    file://remove.autoconf.version.check.patch \
    file://not-check-libperl.patch \
"

do_compile_prepend_libc-musl() {
    sed -i -e 's/\-lnsl//g' ${B}/src/Makefile.global
}

SRC_URI[md5sum] = "0aada0833a9208ae5fab966c73c39379"
SRC_URI[sha256sum] = "12bfb3c7e8e45515ef921ad365e122682a5c4935dcc0032644433af2de31acc4"
