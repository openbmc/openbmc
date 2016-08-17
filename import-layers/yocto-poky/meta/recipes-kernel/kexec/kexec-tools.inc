SUMMARY = "Kexec fast reboot tools"
DESCRIPTION = "Kexec is a fast reboot feature that lets you reboot to a new Linux kernel"
AUTHOR = "Eric Biederman"
HOMEPAGE = "http://kernel.org/pub/linux/utils/kernel/kexec/"
SECTION = "kernel/userland"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a \
                    file://kexec/kexec.c;beginline=1;endline=20;md5=af10f6ae4a8715965e648aa687ad3e09"
DEPENDS = "zlib xz"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/kernel/kexec/kexec-tools-${PV}.tar.gz \
           file://kdump \
           file://kdump.conf \
"

PR = "r1"

inherit autotools

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*|powerpc.*|mips.*)-(linux|freebsd.*)'

INSANE_SKIP_${PN} = "arch"

do_compile_prepend() {
    # Remove the '*.d' file to make sure the recompile is OK
    for dep in `find ${B} -type f -name '*.d'`; do
        dep_no_d="`echo $dep | sed 's#.d$##'`"
        # Remove file.d when there is a file.o
        if [ -f "$dep_no_d.o" ]; then
            rm -f $dep
        fi
    done
}
