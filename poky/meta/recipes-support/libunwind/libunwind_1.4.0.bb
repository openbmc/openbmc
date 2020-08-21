require libunwind.inc

SRC_URI = "http://download.savannah.nongnu.org/releases/libunwind/libunwind-${PV}.tar.gz \
           file://0001-Add-AO_REQUIRE_CAS-to-fix-build-on-ARM-v6.patch \
           file://0002-backtrace-Use-only-with-glibc-and-uclibc.patch \
           file://0003-x86-Stub-out-x86_local_resume.patch \
           file://0004-Fix-build-on-mips-musl.patch \
           file://0005-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           file://0006-Fix-for-X32.patch \
           file://sigset_t.patch \
           file://0001-Fix-compilation-with-fno-common.patch \
           "
SRC_URI_append_libc-musl = " file://musl-header-conflict.patch"

SRC_URI[md5sum] = "5114504c74ac3992ac06aa551cd55678"
SRC_URI[sha256sum] = "df59c931bd4d7ebfd83ee481c943edf015138089b8e50abed8d9c57ba9338435"

EXTRA_OECONF_append_libc-musl = " --disable-documentation --disable-tests --enable-static"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

SECURITY_LDFLAGS_append_libc-musl = " -lssp_nonshared"
