require libunwind.inc

SRC_URI = "http://download.savannah.nongnu.org/releases/libunwind/libunwind-${PV}.tar.gz \
           file://0001-Add-AO_REQUIRE_CAS-to-fix-build-on-ARM-v6.patch \
           file://0003-x86-Stub-out-x86_local_resume.patch \
           file://0004-Fix-build-on-mips-musl.patch \
           file://0005-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           file://0006-Fix-for-X32.patch \
           "
SRC_URI_append_libc-musl = " file://musl-header-conflict.patch"

SRC_URI[md5sum] = "c6923dda0675f6a4ef21426164dc8b6a"
SRC_URI[sha256sum] = "90337653d92d4a13de590781371c604f9031cdb50520366aa1e3a91e1efb1017"

EXTRA_OECONF_append_libc-musl = " --disable-documentation --disable-tests --enable-static"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

SECURITY_LDFLAGS_append_libc-musl = " -lssp_nonshared"
