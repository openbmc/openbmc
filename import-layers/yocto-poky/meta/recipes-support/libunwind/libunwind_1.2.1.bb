require libunwind.inc

SRC_URI[md5sum] = "06ba9e60d92fd6f55cd9dadb084df19e"
SRC_URI[sha256sum] = "3f3ecb90e28cbe53fba7a4a27ccce7aad188d3210bb1964a923a731a27a75acb"

SRC_URI = "http://download.savannah.nongnu.org/releases/libunwind/libunwind-${PV}.tar.gz \
           file://Add-AO_REQUIRE_CAS-to-fix-build-on-ARM-v6.patch \
           file://0001-backtrace-Use-only-with-glibc-and-uclibc.patch \
           file://0001-x86-Stub-out-x86_local_resume.patch \
           file://0001-Fix-build-on-mips-musl.patch \
           file://0001-add-knobs-to-disable-enable-tests.patch \
           file://0001-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           file://libunwind-1.1-x32.patch \
           "

SRC_URI_append_libc-musl = " file://musl-header-conflict.patch"
EXTRA_OECONF_append_libc-musl = " --disable-documentation --disable-tests "

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"
