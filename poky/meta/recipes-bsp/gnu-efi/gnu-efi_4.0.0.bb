SUMMARY = "Libraries for producing EFI binaries"
HOMEPAGE = "http://sourceforge.net/projects/gnu-efi/"
DESCRIPTION = "GNU-EFI aims to Develop EFI applications for ARM-64, ARM-32, x86_64, IA-64 (IPF), IA-32 (x86), and MIPS platforms using the GNU toolchain and the EFI development environment."
SECTION = "devel"
LICENSE = "GPL-2.0-or-later & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://gnuefi/crt0-efi-arm.S;beginline=4;endline=16;md5=8b0a86085b86eda7a3c7e8a1eb7ec753 \
                    file://gnuefi/crt0-efi-aarch64.S;beginline=4;endline=16;md5=8b0a86085b86eda7a3c7e8a1eb7ec753 \
                    file://inc/efishellintf.h;beginline=13;endline=20;md5=ee14c1530c341a7050837adead6bc9a5 \
                    file://lib/arm/math.c;beginline=2;endline=15;md5=ccb5c6b51053d1ee7277539ec38513d7 \
                    file://lib/arm/initplat.c;beginline=2;endline=15;md5=ccb5c6b51053d1ee7277539ec38513d7 \
                    file://lib/aarch64/math.c;beginline=2;endline=15;md5=ccb5c6b51053d1ee7277539ec38513d7 \
                    file://lib/aarch64/initplat.c;beginline=2;endline=15;md5=ccb5c6b51053d1ee7277539ec38513d7 \
                   "

COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*|riscv64.*)-linux"
COMPATIBLE_HOST:armv4 = 'null'

SRC_URI = "git://github.com/ncroxon/gnu-efi;protocol=https;branch=master \
           file://0002-Do-not-treat-warnings-as-errors.patch \
           "
SRCREV = "5ea320f0f01c8de8f9dd4e4e38a245608f0287dd"

S = "${WORKDIR}/git"

inherit github-releases

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

EXTRA_OEMAKE = "'V=1' 'ARCH=${@gnu_efi_arch(d)}' \
                'HOSTCC=${BUILD_CC}' 'CC=${CC}' \
                'AS=${AS}' 'LD=${LD}' 'AR=${AR}' \
                'RANLIB=${RANLIB}' 'OBJCOPY=${OBJCOPY}' \
                'PREFIX=${prefix}' 'LIBDIR=${libdir}' 'INCLUDEDIR=${includedir}' \
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
