require libunwind.inc

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://Support-building-with-older-compilers.patch \
           file://AArch64-port.patch \
           file://Fix-test-case-link-failure-on-PowerPC-systems-with-Altivec.patch \
           file://Link-libunwind-to-libgcc_s-rather-than-libgcc.patch \
           file://0001-Invalid-dwarf-opcodes-can-cause-references-beyond-th.patch \
           file://Add-AO_REQUIRE_CAS-to-fix-build-on-ARM-v6.patch \
           file://0001-backtrace-Use-only-with-glibc-and-uclibc.patch \
"
SRC_URI_append_libc-musl = "\
           file://0001-x86-Stub-out-x86_local_resume.patch \
           file://0001-disable-tests.patch \
           file://0001-Fix-build-on-mips-musl.patch \
"
SRC_URI[md5sum] = "fb4ea2f6fbbe45bf032cd36e586883ce"
SRC_URI[sha256sum] = "9dfe0fcae2a866de9d3942c66995e4b460230446887dbdab302d41a8aee8d09a"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
