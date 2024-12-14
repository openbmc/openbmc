SUMMARY = "Tool Command Language"
HOMEPAGE = "http://tcl.sourceforge.net"
DESCRIPTION = "Tool Command Language, is an open-source multi-purpose C library which includes a powerful dynamic scripting language. Together they provide ideal cross-platform development environment for any programming project."
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "TCL & BSD-3-Clause"
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
           file://0005-tcl-fix-race-in-interp.test.patch \
           "
SRC_URI[sha256sum] = "3186e23c7417359d90e3c46f531d442c76d3c05a2dba1081c02b75e32908b2b7"

UPSTREAM_CHECK_URI = "https://www.tcl.tk/software/tcltk/download.html"
UPSTREAM_CHECK_REGEX = "tcl(?P<pver>\d+(\.\d+)+)-src"

S = "${WORKDIR}/${BPN}${PV}"

VER = "${PV}"

inherit autotools ptest binconfig

AUTOTOOLS_SCRIPT_PATH = "${S}/unix"
EXTRA_OECONF = "--disable-rpath --enable-man-suffix=tcl9"

# Prevent installing copy of tzdata based on tzdata installation on the build host
# It doesn't install tzdata if one of the following files exist on the host:
# /usr/share/zoneinfo/UTC /usr/share/zoneinfo/GMT /usr/share/lib/zoneinfo/UTC /usr/share/lib/zoneinfo/GMT /usr/lib/zoneinfo/UTC /usr/lib/zoneinfo/GMT
# otherwise "/usr/lib/tcl8.6/tzdata" is included in tcl package
EXTRA_OECONF += "--with-tzdata=no"

do_install() {
	autotools_do_install
	oe_runmake 'DESTDIR=${D}' install-private-headers
	ln -sf ./tclsh${VER} ${D}${bindir}/tclsh
	ln -sf tclsh9.0 ${D}${bindir}/tclsh${VER}
	sed -i "s;-L${B};-L${STAGING_LIBDIR};g" tclConfig.sh
	sed -i "s;'${WORKDIR};'${STAGING_INCDIR};g" tclConfig.sh
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
FILES:tcl-lib = "${libdir}/libtcl9.0.so.*"
FILES:${PN} += "${libdir}/tcl${VER} ${libdir}/tcl9.0 ${libdir}/tcl9"
FILES:${PN}-dev += "${libdir}/tclConfig.sh ${libdir}/tclooConfig.sh"

# isn't getting picked up by shlibs code
RDEPENDS:${PN} += "tcl-lib"
RDEPENDS:${PN}-ptest += "libgcc locale-base-en-us tzdata"

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
	oe_runmake tcltest
}

do_install_ptest() {
	cp ${B}/tcltest ${D}${PTEST_PATH}
	cp -r ${S}/library ${D}${PTEST_PATH}
	cp -r ${S}/tests ${D}${PTEST_PATH}
}

do_install_ptest:append:libc-musl () {
	# Assumes locales other than provided by musl-locales
	sed -i '/SKIP="$SKIP socket.*$/a # unixInit-3* is suppressed due to hardcoded locale assumptions\nSKIP="$SKIP unixInit-3\\\*"' ${D}${PTEST_PATH}/run-ptest
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
	       ${PKGD}${libdir}/tclConfig.sh

	rm -f ${PKGD}${bindir_crossscripts}/tclConfig.sh
}
