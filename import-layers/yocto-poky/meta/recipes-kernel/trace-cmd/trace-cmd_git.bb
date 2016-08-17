SUMMARY = "User interface to Ftrace"
LICENSE = "GPLv2 & LGPLv2.1"

require trace-cmd.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://trace-cmd.c;beginline=6;endline=8;md5=2c22c965a649ddd7973d7913c5634a5e \
                    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://trace-input.c;beginline=5;endline=8;md5=3ec82f43bbe0cfb5951ff414ef4d44d0 \
"

EXTRA_OEMAKE = "\
    'prefix=${prefix}' \
    'bindir=${bindir}' \
    'man_dir=${mandir}' \
    'html_install=${datadir}/kernelshark/html' \
    'img_install=${datadir}/kernelshark/html/images' \
    \
    'bindir_relative=${@oe.path.relative(prefix, bindir)}' \
    'libdir=${@oe.path.relative(prefix, libdir)}' \
    \
    NO_PYTHON=1 \
"

do_compile_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.*.d
}

do_install() {
        oe_runmake DESTDIR="${D}" install
}

