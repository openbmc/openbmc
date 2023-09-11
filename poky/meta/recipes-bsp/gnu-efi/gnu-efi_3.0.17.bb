SUMMARY = "Libraries for producing EFI binaries"
HOMEPAGE = "http://sourceforge.net/projects/gnu-efi/"
DESCRIPTION = "GNU-EFI aims to Develop EFI applications for ARM-64, ARM-32, x86_64, IA-64 (IPF), IA-32 (x86), and MIPS platforms using the GNU toolchain and the EFI development environment."
SECTION = "devel"
LICENSE = "GPL-2.0-or-later | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://gnuefi/crt0-efi-arm.S;beginline=4;endline=16;md5=e582764a4776e60c95bf9ab617343d36 \
                    file://gnuefi/crt0-efi-aarch64.S;beginline=4;endline=16;md5=e582764a4776e60c95bf9ab617343d36 \
                    file://inc/efishellintf.h;beginline=13;endline=20;md5=202766b79d708eff3cc70fce15fb80c7 \
                    file://lib/arm/math.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/arm/initplat.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/aarch64/math.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/aarch64/initplat.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                   "

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/files/${BP}.tar.bz2 \
           file://parallel-make-archives.patch \
           file://gnu-efi-3.0.9-fix-clang-build.patch \
           file://0001-riscv64-adjust-type-definitions.patch \
           file://0001-riscv64-ignore-unknown-relocs.patch \
           file://no-werror.patch \
           "
SRC_URI[sha256sum] = "7807e903349343a7a142ebb934703a2872235e89688cf586c032b0a1087bcaf4"

COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*|riscv64.*)-linux"
COMPATIBLE_HOST:armv4 = 'null'

do_configure:linux-gnux32:prepend() {
	cp ${STAGING_INCDIR}/gnu/stubs-x32.h ${STAGING_INCDIR}/gnu/stubs-64.h
	cp ${STAGING_INCDIR}/bits/long-double-32.h ${STAGING_INCDIR}/bits/long-double-64.h
}

def gnu_efi_arch(d):
    import re
    tarch = d.getVar("TARGET_ARCH")
    if re.match("i[3456789]86", tarch):
        return "ia32"
    return tarch

do_compile:prepend() {
    unset LDFLAGS
}

EXTRA_OEMAKE = "'ARCH=${@gnu_efi_arch(d)}' 'CC=${CC}' 'AS=${AS}' 'LD=${LD}' 'AR=${AR}' \
                'RANLIB=${RANLIB}' 'OBJCOPY=${OBJCOPY}' 'PREFIX=${prefix}' 'LIBDIR=${libdir}' \
                "

# gnu-efi's Makefile treats prefix as toolchain prefix, so don't
# export it.
prefix[unexport] = "1"

do_install() {
        oe_runmake install INSTALLROOT="${D}"
}

FILES:${PN} += "${libdir}/*.lds ${libdir}/gnuefi/apps"

# 64-bit binaries are expected for EFI when targeting X32
INSANE_SKIP:${PN}-dev:append:linux-gnux32 = " arch"
INSANE_SKIP:${PN}-dev:append:linux-muslx32 = " arch"

BBCLASSEXTEND = "native"

# It doesn't support sse, its make.defaults sets:
# CFLAGS += -mno-mmx -mno-sse
# So also remove -mfpmath=sse from TUNE_CCARGS
TUNE_CCARGS:remove = "-mfpmath=sse"

python () {
    ccargs = d.getVar('TUNE_CCARGS').split()
    if '-mx32' in ccargs:
        # use x86_64 EFI ABI
        ccargs.remove('-mx32')
        ccargs.append('-m64')
        d.setVar('TUNE_CCARGS', ' '.join(ccargs))
}
