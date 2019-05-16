require glibc.inc

LIC_FILES_CHKSUM = "file://LICENSES;md5=cfc0ed77a9f62fa62eded042ebe31d72 \
      file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

DEPENDS += "gperf-native bison-native make-native"

PV = "2.29"

SRCREV ?= "86013ef5cea322b8f4b9c22f230c22cce369e947"

SRCBRANCH ?= "release/${PV}/master"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"

NATIVESDKFIXES ?= ""
NATIVESDKFIXES_class-nativesdk = "\
           file://0001-nativesdk-glibc-Look-for-host-system-ld.so.cache-as-.patch \
           file://0002-nativesdk-glibc-Fix-buffer-overrun-with-a-relocated-.patch \
           file://0003-nativesdk-glibc-Raise-the-size-of-arrays-containing-.patch \
           file://0004-nativesdk-glibc-Allow-64-bit-atomics-for-x86.patch \
           file://0005-nativesdk-glibc-Make-relocatable-install-for-locales.patch \
"

SRC_URI = "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           file://etc/ld.so.conf \
           file://generate-supported.mk \
           file://makedbs.sh \
           \
           ${NATIVESDKFIXES} \
           file://0006-fsl-e500-e5500-e6500-603e-fsqrt-implementation.patch \
           file://0007-readlib-Add-OECORE_KNOWN_INTERPRETER_NAMES-to-known-.patch \
           file://0008-ppc-sqrt-Fix-undefined-reference-to-__sqrt_finite.patch \
           file://0009-__ieee754_sqrt-f-are-now-inline-functions-and-call-o.patch \
           file://0010-Quote-from-bug-1443-which-explains-what-the-patch-do.patch \
           file://0011-eglibc-run-libm-err-tab.pl-with-specific-dirs-in-S.patch \
           file://0012-__ieee754_sqrt-f-are-now-inline-functions-and-call-o.patch \
           file://0013-sysdeps-gnu-configure.ac-handle-correctly-libc_cv_ro.patch \
           file://0014-Add-unused-attribute.patch \
           file://0015-yes-within-the-path-sets-wrong-config-variables.patch \
           file://0016-timezone-re-written-tzselect-as-posix-sh.patch \
           file://0017-Remove-bash-dependency-for-nscd-init-script.patch \
           file://0018-eglibc-Cross-building-and-testing-instructions.patch \
           file://0019-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0020-eglibc-Clear-cache-lines-on-ppc8xx.patch \
           file://0021-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0022-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0023-Define-DUMMY_LOCALE_T-if-not-defined.patch \
           file://0024-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
           file://0025-elf-dl-deps.c-Make-_dl_build_local_scope-breadth-fir.patch \
           file://0028-intl-Emit-no-lines-in-bison-generated-files.patch \
           file://0029-inject-file-assembly-directives.patch \
           file://0030-locale-prevent-maybe-uninitialized-errors-with-Os-BZ.patch \
           file://0001-x86-64-memcmp-Use-unsigned-Jcc-instructions-on-size-.patch \
           file://CVE-2019-9169.patch \
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

do_compile () {
	# -Wl,-rpath-link <staging>/lib in LDFLAGS can cause breakage if another glibc is in staging
	LDFLAGS="-fuse-ld=bfd"
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
