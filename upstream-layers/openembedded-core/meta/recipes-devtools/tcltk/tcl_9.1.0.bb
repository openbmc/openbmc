SUMMARY = "Tool Command Language"
HOMEPAGE = "http://tcl.sourceforge.net"
DESCRIPTION = "Tool Command Language, is an open-source multi-purpose C library which includes a powerful dynamic scripting language. Together they provide ideal cross-platform development environment for any programming project."
SECTION = "devel/tcltk"

# http://www.tcl-lang.org/software/tcltk/license.html
LICENSE = "BSD-3-Clause AND TCL"
LIC_FILES_CHKSUM = "file://license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://compat/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://library/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://macosx/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://tests/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://win/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
"

DEPENDS = "tcl-native zlib"

SRC_URI = "${SOURCEFORGE_MIRROR}/tcl/tcl-core${PV}-src.tar.gz \
           file://run-ptest \
           file://0001-tcl-Add-tcltk-from-OE.dev-but-with-legacy-staging-fu.patch \
           file://0002-tcl-fix-a-build-issue.patch \
           file://0003-tcl-install-tcl-to-lib64-instead-of-lib-on-64bit-tar.patch \
           file://0004-tcl-update-the-header-location.patch \
           "
SRC_URI[sha256sum] = "ba11ae7035ec4b0caf2c489b20d5aa2abb1cf001a7e65b0fa2fbac86908c643b"

UPSTREAM_CHECK_URI = "https://www.tcl-lang.org/software/tcltk/download.html"
UPSTREAM_CHECK_REGEX = "tcl(?P<pver>\d+(\.\d+)+)-src"

S = "${UNPACKDIR}/${BPN}${PV}"

VER = "${PV}"

inherit autotools ptest binconfig

AUTOTOOLS_SCRIPT_PATH = "${S}/unix"

EXTRA_OECONF = "--disable-rpath --enable-man-suffix=tcl9 --disable-zipfs"

# Override some paths to not include TMPDIR into binaries
EXTRA_OEMAKE = "ABS_BUILD_DIR=/build TCL_BUILDTIME_LIBRARY=/build/lib"

PACKAGECONFIG ??= ""
# Use of system tzdata is not recommended at present:
# https://core.tcl-lang.org/tcl/tktview/51aa53616067cb63900b17ca1d71f07b094ffa1a
PACKAGECONFIG[system-tzdata] = "--with-tzdata=no,--with-tzdata=yes,,tzdata"

do_install() {
	autotools_do_install
	oe_runmake 'DESTDIR=${D}' install-private-headers
	ln -sf ./tclsh${VER} ${D}${bindir}/tclsh
	ln -sf tclsh9.1 ${D}${bindir}/tclsh${VER}
	sed -i "s;-L${B};-L${STAGING_LIBDIR};g" tclConfig.sh
	sed -i "s;'${UNPACKDIR};'${STAGING_INCDIR};g" tclConfig.sh
	install -d ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${libdir}
	for dir in compat generic unix; do
		install -d ${D}${includedir}/${BPN}${VER}/$dir
		install -m 0644 ${S}/$dir/*.h ${D}${includedir}/${BPN}${VER}/$dir/
	done
}

SYSROOT_DIRS += "${bindir_crossscripts}"

PACKAGES =+ "tcl-lib"
FILES:tcl-lib = "${libdir}/libtcl9.1.so.*"
FILES:${PN} += "${libdir}/tcl${VER} ${libdir}/tcl9.1 ${libdir}/tcl9"
FILES:${PN}-dev += "${libdir}/tclConfig.sh ${libdir}/tclooConfig.sh"

# isn't getting picked up by shlibs code
RDEPENDS:${PN} += "tcl-lib"
RDEPENDS:${PN}-ptest += "libgcc locale-base-en-us"

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
	oe_runmake tcltest
}

# Override-specific skips; do_install_ptest needs the empty SKIP="" in run-ptest
SKIPPED_TESTS = ""
# Assumes locales other than provided by musl-locales
SKIPPED_TESTS:append:libc-musl = " unixInit-3*"
# RISC-V widens a negative float NaN to the positive canonical NaN, so the
# sign check fails: https://core.tcl-lang.org/tcl/tktview/bf029ced86
SKIPPED_TESTS:append:riscv32 = " binary-40.3"
SKIPPED_TESTS:append:riscv64 = " binary-40.3"

do_install_ptest() {
	cp ${B}/tcltest ${D}${PTEST_PATH}
	cp -r ${S}/tests ${D}${PTEST_PATH}
	sed -i -e 's|^SKIP=""$|SKIP="${SKIPPED_TESTS}"|' ${D}${PTEST_PATH}/run-ptest
}

# Fix some paths that might be used by Tcl extensions
BINCONFIG_GLOB = "*Config.sh"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"

# Cleanup host path from ${libdir}/tclConfig.sh and remove the
# ${bindir_crossscripts}/tclConfig.sh from target
PACKAGE_PREPROCESS_FUNCS += "tcl_package_preprocess"
tcl_package_preprocess() {
	sed -i -e "s;${DEBUG_PREFIX_MAP};;g" \
	       -e "s;-L${STAGING_LIBDIR};-L${libdir};g" \
	       -e "s;${STAGING_INCDIR};${includedir};g" \
	       -e "s;--sysroot=${RECIPE_SYSROOT};;g" \
	       -e "s;${B};${libdir};g" ${PKGD}${libdir}/tclConfig.sh \
	       ${PKGD}${libdir}/tclConfig.sh

	rm -f ${PKGD}${bindir_crossscripts}/tclConfig.sh
}
