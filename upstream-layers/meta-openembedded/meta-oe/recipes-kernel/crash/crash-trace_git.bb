SUMMARY = "trace extension module for crash"
DESCRIPTION = "Displays kernel tracing data and traced events that occurred prior to a panic."

HOMEPAGE = "https://github.com/fujitsu/crash-trace"
SECTION = "devel"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "crash zlib readline ncurses"
RDEPENDS:${PN} = "crash"

SRC_URI = "git://github.com/fujitsu/crash-trace.git;branch=master;protocol=https"
SRCREV = "2106a9d04020192f6f286171a060c4d2300059b7"
PV = "3.0+git"

TARGET_CC_ARCH:append = " ${SELECTED_OPTIMIZATION}"

# crash 7.1.3 and before don't support mips64/riscv64
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:mipsarchn64 = "null"
COMPATIBLE_HOST:mipsarchn32 = "null"

python () {
    arch = d.getVar('TARGET_ARCH')
    makefile_arch = ""
    if arch.startswith('x86_64'):
        makefile_arch = "X86_64"
    elif arch == 'aarch64':
        makefile_arch = "ARM64"
    elif arch == 'i686':
        makefile_arch = "i686"
    elif arch == 'i586':
        makefile_arch = "X86"

    d.setVar('TARGET_ARCH_FOR_MAKEFILE', makefile_arch)
}

EXTRA_OEMAKE = 'TARGET=${TARGET_ARCH_FOR_MAKEFILE}'

do_configure() {
    :
}

do_compile:prepend() {
    sed -i -E 's,^([ \t]*)gcc ,\1${CC} ${CFLAGS} ,' ${S}/Makefile
    sed -i 's,INCDIR=/usr/include/crash,INCDIR=${STAGING_INCDIR}/crash,' ${S}/Makefile
    sed -i 's|-o trace.so|${LDFLAGS} -o trace.so|' ${S}/Makefile
}

ARM_INSTRUCTION_SET = "arm"

COMPATIBLE_HOST:libc-musl = 'null'

do_install () {
    install -d ${D}${libdir}/crash/extensions/

    install -m 0644 ${S}/trace.so ${D}/${libdir}/crash/extensions/
}

FILES:${PN} += " \
    ${libdir}/crash/extensions/ \
"

