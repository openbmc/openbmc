SUMMARY = "Libraries for producing EFI binaries"
HOMEPAGE = "http://sourceforge.net/projects/gnu-efi/"
SECTION = "devel"
LICENSE = "GPLv2+ | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://gnuefi/crt0-efi-arm.S;beginline=4;endline=16;md5=e582764a4776e60c95bf9ab617343d36 \
                    file://gnuefi/crt0-efi-aarch64.S;beginline=4;endline=16;md5=e582764a4776e60c95bf9ab617343d36 \
                    file://inc/efishellintf.h;beginline=13;endline=20;md5=202766b79d708eff3cc70fce15fb80c7 \
                    file://inc/efishellparm.h;beginline=4;endline=11;md5=468b1231b05bbc84bae3a0d5774e3bb5 \
                    file://lib/arm/math.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/arm/initplat.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/aarch64/math.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                    file://lib/aarch64/initplat.c;beginline=2;endline=15;md5=8ed772501da77b2b3345aa6df8744c9e \
                   "

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/files/${BP}.tar.bz2 \
           file://parallel-make-archives.patch \
           file://lib-Makefile-fix-parallel-issue.patch \
           file://gnu-efi-3.0.9-fix-clang-build.patch \
           "

SRC_URI[md5sum] = "926763ff37bc9db3a9035cec41eb2f45"
SRC_URI[sha256sum] = "0196f2e1fd3c334b66e610a608a0e59233474c7a01bec7bc53989639aa327669"

COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"
COMPATIBLE_HOST_armv4 = 'null'

do_configure_linux-gnux32_prepend() {
	cp ${STAGING_INCDIR}/gnu/stubs-x32.h ${STAGING_INCDIR}/gnu/stubs-64.h
	cp ${STAGING_INCDIR}/bits/long-double-32.h ${STAGING_INCDIR}/bits/long-double-64.h
}

def gnu_efi_arch(d):
    import re
    tarch = d.getVar("TARGET_ARCH")
    if re.match("i[3456789]86", tarch):
        return "ia32"
    return tarch

EXTRA_OEMAKE = "'ARCH=${@gnu_efi_arch(d)}' 'CC=${CC}' 'AS=${AS}' 'LD=${LD}' 'AR=${AR}' \
                'RANLIB=${RANLIB}' 'OBJCOPY=${OBJCOPY}' 'PREFIX=${prefix}' 'LIBDIR=${libdir}' \
                "

# gnu-efi's Makefile treats prefix as toolchain prefix, so don't
# export it.
prefix[unexport] = "1"

do_install() {
        oe_runmake install INSTALLROOT="${D}"
}

FILES_${PN} += "${libdir}/*.lds"

# 64-bit binaries are expected for EFI when targeting X32
INSANE_SKIP_${PN}-dev_append_linux-gnux32 = " arch"
INSANE_SKIP_${PN}-dev_append_linux-muslx32 = " arch"

BBCLASSEXTEND = "native"

# It doesn't support sse, its make.defaults sets:
# CFLAGS += -mno-mmx -mno-sse
# So also remove -mfpmath=sse from TUNE_CCARGS
TUNE_CCARGS_remove = "-mfpmath=sse"

python () {
    ccargs = d.getVar('TUNE_CCARGS').split()
    if '-mx32' in ccargs:
        # use x86_64 EFI ABI
        ccargs.remove('-mx32')
        ccargs.append('-m64')
        d.setVar('TUNE_CCARGS', ' '.join(ccargs))
}
