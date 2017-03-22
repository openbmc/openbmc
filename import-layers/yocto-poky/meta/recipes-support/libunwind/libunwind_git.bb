require libunwind.inc

PV = "1.1+git${SRCPV}"

SRCREV = "bc8698fd7ed13a629a8ec3cb2a89bd74f9d8b5c0"

SRC_URI = "git://git.sv.gnu.org/libunwind.git \
           file://Add-AO_REQUIRE_CAS-to-fix-build-on-ARM-v6.patch \
           file://0001-backtrace-Use-only-with-glibc-and-uclibc.patch \
           file://0001-x86-Stub-out-x86_local_resume.patch \
           file://0001-Fix-build-on-mips-musl.patch \
           file://0001-add-knobs-to-disable-enable-tests.patch \
           file://0001-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           "

SRC_URI_append_libc-musl = " file://musl-header-conflict.patch"
EXTRA_OECONF_append_libc-musl = " --disable-documentation --disable-tests "

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

# see https://sourceware.org/bugzilla/show_bug.cgi?id=19987
SECURITY_CFLAGS_remove_aarch64 = "-fpie"
SECURITY_CFLAGS_append_aarch64 = " -fPIE"

S = "${WORKDIR}/git"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"
