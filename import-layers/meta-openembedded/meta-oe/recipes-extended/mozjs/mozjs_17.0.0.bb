SUMMARY = "SpiderMonkey is Mozilla's JavaScript engine written in C/C++"
HOMEPAGE = "http://www.mozilla.org/js/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "http://ftp.mozilla.org/pub/mozilla.org/js/${BPN}${PV}.tar.gz \
           file://0001-mozjs17.0.0-fix-the-compile-bug-of-powerpc.patch \
           file://0001-js.pc.in-do-not-include-RequiredDefines.h-for-depend.patch \
           file://0002-Move-JS_BYTES_PER_WORD-out-of-config.h.patch;patchdir=../../ \
           file://0003-Add-AArch64-support.patch;patchdir=../../ \
           file://0004-mozbug746112-no-decommit-on-large-pages.patch;patchdir=../../ \
           file://0005-aarch64-64k-page.patch;patchdir=../../ \
           file://0001-regenerate-configure.patch;patchdir=../../ \
           file://fix-the-compile-error-of-powerpc64.patch;patchdir=../../ \
           file://fix_milestone_compile_issue.patch \
           file://0010-fix-cross-compilation-on-i586-targets.patch;patchdir=../../ \
           file://Manually_mmap_heap_memory_esr17.patch;patchdir=../../ \
           file://0001-compare-the-first-character-of-string-to-be-null-or-.patch;patchdir=../../ \
           "

SRC_URI[md5sum] = "20b6f8f1140ef6e47daa3b16965c9202"
SRC_URI[sha256sum] = "321e964fe9386785d3bf80870640f2fa1c683e32fe988eeb201b04471c172fba"

S = "${WORKDIR}/${BPN}${PV}/js/src"

inherit autotools pkgconfig perlnative pythonnative

DEPENDS += "nspr zlib"

# Host specific flags need to be defined, otherwise target flags will be passed to the host
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
export HOST_LDFLAGS = "${BUILD_LDFLAGS}"

# nspr's package-config is ignored so set libs manually
EXTRA_OECONF = " \
    --target=${TARGET_SYS} \
    --host=${BUILD_SYS} \
    --build=${BUILD_SYS} \
    --prefix=${prefix} \
    --libdir=${libdir} \
    --with-nspr-libs='-lplds4 -lplc4 -lnspr4' \
    --enable-threadsafe \
    --disable-static \
"
EXTRA_OECONF_append_armv4 = " \
    --disable-methodjit \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--with-x --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR},--without-x,virtual/libx11"

# mozjs requires autoreconf 2.13
do_configure() {
    export HOST_CFLAGS="${BUILD_CFLAGS}"
    export HOST_CXXFLAGS="${BUILD_CPPFLAGS}"
    export HOST_LDFLAGS="${BUILD_LDFLAGS}"
    ( cd ${S}
      gnu-configize --force
      mv config.guess config.sub build/autoconf )
    ${S}/configure ${EXTRA_OECONF}
}

# patch.bbclass will try to apply the patches already present and fail, so clean them out
do_unpack() {
    tar -xvf ${DL_DIR}/mozjs17.0.0.tar.gz -C ${WORKDIR}/
    rm -rf ${WORKDIR}/${BPN}${PV}/patches
}


PACKAGES =+ "lib${BPN}"
FILES_lib${BPN} += "${libdir}/lib*.so"
FILES_${PN}-dev += "${bindir}/js17-config"

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:2172: Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r2,r1,LSR#20'
#| {standard input}:2173: Error: unshifted register required -- `bic r2,r2,#(1<<11)'
#| {standard input}:2174: Error: unshifted register required -- `orr r1,r1,#(1<<20)'
#| {standard input}:2176: Error: instruction not supported in Thumb16 mode -- `subs r2,r2,#0x300'
#| {standard input}:2178: Error: instruction not supported in Thumb16 mode -- `subs r5,r2,#52'
ARM_INSTRUCTION_SET = "arm"
