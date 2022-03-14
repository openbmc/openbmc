require libunwind.inc

SRC_URI = "http://download.savannah.nongnu.org/releases/libunwind/libunwind-${PV}.tar.gz \
           file://0003-x86-Stub-out-x86_local_resume.patch \
           file://0004-Fix-build-on-mips-musl.patch \
           file://0005-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           file://0006-Fix-for-X32.patch \
           "
SRC_URI:append:libc-musl = " file://musl-header-conflict.patch"

SRC_URI[sha256sum] = "4a6aec666991fb45d0889c44aede8ad6eb108071c3554fcdff671f9c94794976"

EXTRA_OECONF:append:libc-musl = " --disable-documentation --disable-tests --enable-static"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

COMPATIBLE_HOST:riscv32 = "null"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

SECURITY_LDFLAGS:append:libc-musl = " -lssp_nonshared"
CACHED_CONFIGUREVARS:append:libc-musl = " LDFLAGS='${LDFLAGS} -lucontext'"
