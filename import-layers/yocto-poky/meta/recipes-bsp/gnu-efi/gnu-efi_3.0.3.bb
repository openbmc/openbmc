SUMMARY = "Libraries for producing EFI binaries"
HOMEPAGE = "http://sourceforge.net/projects/gnu-efi/"
SECTION = "devel"
LICENSE = "GPLv2+ | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://gnuefi/crt0-efi-arm.S;beginline=4;endline=9;md5=2240d7bbdf0928294c2f4a68b14d6591 \
                    file://gnuefi/crt0-efi-aarch64.S;beginline=4;endline=16;md5=e582764a4776e60c95bf9ab617343d36 \
                    file://inc/efishellintf.h;beginline=13;endline=20;md5=202766b79d708eff3cc70fce15fb80c7 \
                    file://inc/efishellparm.h;beginline=4;endline=11;md5=468b1231b05bbc84bae3a0d5774e3bb5 \
                    file://lib/arm/div64.S;beginline=6;endline=12;md5=a96c84f5ad12b4f011f98b5d039242f2 \
                    file://lib/arm/math.c;beginline=4;endline=10;md5=64dd1987cee1dcf59d11aa572cfa644e \
                    file://lib/arm/initplat.c;beginline=4;endline=10;md5=64dd1987cee1dcf59d11aa572cfa644e \
                    file://lib/arm/lib1funcs.S;beginline=9;endline=33;md5=f56d5ebbc87136bc66cfe24db82bcf01 \
                    file://lib/aarch64/math.c;beginline=9;endline=33;md5=cfade4c560e033a7bb02dab282872c7d \
                    file://lib/aarch64/initplat.c;beginline=9;endline=33;md5=900cb1ffbe3e1ded344102be921830f1 \
                   "

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.bz2 \
           file://parallel-make-archives.patch \
           file://lib-Makefile-fix-parallel-issue.patch \
           file://gnu-efi-Make-setjmp.S-portable-to-ARM.patch \
           file://gcc46-compatibility.patch \
          "

SRC_URI[md5sum] = "15a4bcbc18a9a5e8110ed955970622e6"
SRC_URI[sha256sum] = "c530f21a15fd9c214dd92d29a6caa20fac989289267512020b6da1f5e6f5b4cb"

COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"

def gnu_efi_arch(d):
    import re
    tarch = d.getVar("TARGET_ARCH", True)
    if re.match("i[3456789]86", tarch):
        return "ia32"
    return tarch

EXTRA_OEMAKE = "'ARCH=${@gnu_efi_arch(d)}' 'CC=${CC}' 'AS=${AS}' 'LD=${LD}' 'AR=${AR}' \
                'RANLIB=${RANLIB}' 'OBJCOPY=${OBJCOPY}' 'PREFIX=${prefix}' 'LIBDIR=${libdir}' \
                "

do_install() {
        oe_runmake install INSTALLROOT="${D}"
}

FILES_${PN} += "${libdir}/*.lds"

BBCLASSEXTEND = "native"
