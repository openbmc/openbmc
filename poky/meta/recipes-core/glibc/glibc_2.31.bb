require glibc.inc
require glibc-version.inc

CVE_CHECK_WHITELIST += "CVE-2020-10029 CVE-2020-6096 CVE-2016-10228 CVE-2020-1751 CVE-2020-1752 \
                        CVE-2021-27645 CVE-2021-3326 CVE-2020-27618 CVE-2020-29562 CVE-2019-25013 \
                        CVE-2022-23218 CVE-2022-23219 \
"

# glibc https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2019-1010022
# glibc https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2019-1010023
# glibc https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2019-1010024
# Upstream glibc maintainers dispute there is any issue and have no plans to address it further.
# "this is being treated as a non-security bug and no real threat."
CVE_CHECK_WHITELIST += "CVE-2019-1010022 CVE-2019-1010023 CVE-2019-1010024"

# glibc https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2019-1010025
# Allows for ASLR bypass so can bypass some hardening, not an exploit in itself, may allow
# easier access for another. "ASLR bypass itself is not a vulnerability."
# Potential patch at https://sourceware.org/bugzilla/show_bug.cgi?id=22853
CVE_CHECK_WHITELIST += "CVE-2019-1010025"

# glibc https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2021-35942
# The wordexp function in the GNU C Library (aka glibc) through 2.33 may crash
# or read arbitrary memory in parse_param (in posix/wordexp.c) when called with
# an untrusted, crafted pattern, potentially resulting in a denial of service
# or disclosure of information. Patch was backported to 2.31 branch already:
# https://sourceware.org/git/?p=glibc.git;a=commit;h=4f0a61f75385c9a5879cbe7202042e88f692a3c8
# which is already included in the dunfell branch of poky:
# https://git.yoctoproject.org/cgit/cgit.cgi/poky/commit/?h=dunfell&id=e1e89ff7d75c3d2223f9e3bd875b9b0c5e15836b
CVE_CHECK_WHITELIST += "CVE-2021-35942"

DEPENDS += "gperf-native bison-native make-native"

NATIVESDKFIXES ?= ""
NATIVESDKFIXES_class-nativesdk = "\
           file://0003-nativesdk-glibc-Look-for-host-system-ld.so.cache-as-.patch \
           file://0004-nativesdk-glibc-Fix-buffer-overrun-with-a-relocated-.patch \
           file://0005-nativesdk-glibc-Raise-the-size-of-arrays-containing-.patch \
           file://0006-nativesdk-glibc-Allow-64-bit-atomics-for-x86.patch \
           file://0007-nativesdk-glibc-Make-relocatable-install-for-locales.patch \
"

SRC_URI =  "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           file://etc/ld.so.conf \
           file://generate-supported.mk \
           file://makedbs.sh \
           \
           ${NATIVESDKFIXES} \
           file://0008-fsl-e500-e5500-e6500-603e-fsqrt-implementation.patch \
           file://0009-readlib-Add-OECORE_KNOWN_INTERPRETER_NAMES-to-known-.patch \
           file://0010-ppc-sqrt-Fix-undefined-reference-to-__sqrt_finite.patch \
           file://0011-__ieee754_sqrt-f-are-now-inline-functions-and-call-o.patch \
           file://0012-Quote-from-bug-1443-which-explains-what-the-patch-do.patch \
           file://0013-eglibc-run-libm-err-tab.pl-with-specific-dirs-in-S.patch \
           file://0014-__ieee754_sqrt-f-are-now-inline-functions-and-call-o.patch \
           file://0015-sysdeps-gnu-configure.ac-handle-correctly-libc_cv_ro.patch \
           file://0017-yes-within-the-path-sets-wrong-config-variables.patch \
           file://0018-timezone-re-written-tzselect-as-posix-sh.patch \
           file://0019-Remove-bash-dependency-for-nscd-init-script.patch \
           file://0020-eglibc-Cross-building-and-testing-instructions.patch \
           file://0021-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0022-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0023-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0024-Define-DUMMY_LOCALE_T-if-not-defined.patch \
           file://0025-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
           file://0026-elf-dl-deps.c-Make-_dl_build_local_scope-breadth-fir.patch \
           file://0027-intl-Emit-no-lines-in-bison-generated-files.patch \
           file://0028-inject-file-assembly-directives.patch \
           file://0029-locale-prevent-maybe-uninitialized-errors-with-Os-BZ.patch \
           file://CVE-2020-29573.patch \
           file://CVE-2021-33574_1.patch \
           file://CVE-2021-33574_2.patch \
           file://CVE-2021-38604.patch \
           file://0030-elf-Refactor_dl_update-slotinfo-to-avoid-use-after-free.patch \
           file://0031-elf-Fix-data-races-in-pthread_create-and-TLS-access-BZ-19329.patch \
           file://0032-elf-Use-relaxed-atomics-for-racy-accesses-BZ-19329.patch \
           file://0033-elf-Add-test-case-for-BZ-19329.patch \
           file://0034-elf-Fix-DTV-gap-reuse-logic-BZ-27135.patch \
           file://0035-x86_64-Avoid-lazy-relocation-of-tlsdesc-BZ-27137.patch \
           file://0036-i386-Avoid-lazy-relocation-of-tlsdesc-BZ-27137.patch \
           file://0037-Avoid-deadlock-between-pthread_create-and-ctors.patch \
           "
S = "${WORKDIR}/git"
B = "${WORKDIR}/build-${TARGET_SYS}"

PACKAGES_DYNAMIC = ""

# the -isystem in bitbake.conf screws up glibc do_stage
BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE}"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir}"

GLIBC_BROKEN_LOCALES = ""

GLIBCPIE ??= ""

EXTRA_OECONF = "--enable-kernel=${OLDEST_KERNEL} \
                --disable-profile \
                --disable-debug --without-gd \
                --enable-clocale=gnu \
                --with-headers=${STAGING_INCDIR} \
                --without-selinux \
                --enable-tunables \
                --enable-bind-now \
                --enable-stack-protector=strong \
                --enable-stackguard-randomization \
                --disable-crypt \
                --with-default-link \
                --enable-nscd \
                ${@bb.utils.contains_any('SELECTED_OPTIMIZATION', '-O0 -Og', '--disable-werror', '', d)} \
                ${GLIBCPIE} \
                ${GLIBC_EXTRA_OECONF}"

EXTRA_OECONF += "${@get_libc_fpu_setting(bb, d)}"

do_patch_append() {
    bb.build.exec_func('do_fix_readlib_c', d)
}

do_fix_readlib_c () {
	sed -i -e 's#OECORE_KNOWN_INTERPRETER_NAMES#${EGLIBC_KNOWN_INTERPRETER_NAMES}#' ${S}/elf/readlib.c
}

do_configure () {
# override this function to avoid the autoconf/automake/aclocal/autoheader
# calls for now
# don't pass CPPFLAGS into configure, since it upsets the kernel-headers
# version check and doesn't really help with anything
        (cd ${S} && gnu-configize) || die "failure in running gnu-configize"
        find ${S} -name "configure" | xargs touch
        CPPFLAGS="" oe_runconf
}

LDFLAGS += "-fuse-ld=bfd"
do_compile () {
	base_do_compile
	echo "Adjust ldd script"
	if [ -n "${RTLDLIST}" ]
	then
		prevrtld=`cat ${B}/elf/ldd | grep "^RTLDLIST=" | sed 's#^RTLDLIST="\?\([^"]*\)"\?$#\1#'`
		# remove duplicate entries
		newrtld=`echo $(printf '%s\n' ${prevrtld} ${RTLDLIST} | LC_ALL=C sort -u)`
		echo "ldd \"${prevrtld} ${RTLDLIST}\" -> \"${newrtld}\""
		sed -i ${B}/elf/ldd -e "s#^RTLDLIST=.*\$#RTLDLIST=\"${newrtld}\"#"
	fi
}

require glibc-package.inc

BBCLASSEXTEND = "nativesdk"
