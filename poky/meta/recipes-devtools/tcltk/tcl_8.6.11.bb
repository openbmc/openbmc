SUMMARY = "Tool Command Language"
HOMEPAGE = "http://tcl.sourceforge.net"
DESCRIPTION = "Tool Command Language, is an open-source multi-purpose C library which includes a powerful dynamic scripting language. Together they provide ideal cross-platform development environment for any programming project."
SECTION = "devel/tcltk"

# http://www.tcl.tk/software/tcltk/license.html
LICENSE = "tcl & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../compat/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../library/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../macosx/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../tests/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
    file://../win/license.terms;md5=058f6229798281bbcac4239c788cfa38 \
"

DEPENDS = "tcl-native zlib"

BASE_SRC_URI = "${SOURCEFORGE_MIRROR}/tcl/${BPN}${PV}-src.tar.gz \
                file://tcl-add-soname.patch"
SRC_URI = "${BASE_SRC_URI} \
           file://fix_non_native_build_issue.patch \
           file://fix_issue_with_old_distro_glibc.patch \
           file://no_packages.patch \
           file://tcl-remove-hardcoded-install-path.patch \
           file://alter-includedir.patch \
           file://run-ptest \
"
SRC_URI[sha256sum] = "8c0486668586672c5693d7d95817cb05a18c5ecca2f40e2836b9578064088258"

SRC_URI_class-native = "${BASE_SRC_URI}"

S = "${WORKDIR}/${BPN}${PV}/unix"

PSEUDO_IGNORE_PATHS .= ",${WORKDIR}/${BPN}${PV}"
VER = "${PV}"

inherit autotools ptest binconfig update-alternatives

EXTRA_OECONF = "--enable-threads --disable-rpath --libdir=${libdir}"

do_compile_prepend() {
	echo > ${S}/../compat/fixstrtod.c
}

do_install() {
	autotools_do_install
	oe_runmake 'DESTDIR=${D}' install-private-headers
	ln -sf ./tclsh${VER} ${D}${bindir}/tclsh
	ln -sf tclsh8.6 ${D}${bindir}/tclsh${VER}
	sed -i "s;-L${B};-L${STAGING_LIBDIR};g" tclConfig.sh
	sed -i "s;'${WORKDIR};'${STAGING_INCDIR};g" tclConfig.sh
	install -d ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${libdir}
	for dir in compat generic unix; do
		install -d ${D}${includedir}/${BPN}${VER}/$dir
		install -m 0644 ${S}/../$dir/*.h ${D}${includedir}/${BPN}${VER}/$dir/
	done
}

SYSROOT_DIRS += "${bindir_crossscripts}"

PACKAGES =+ "tcl-lib"
FILES_tcl-lib = "${libdir}/libtcl8.6.so.*"
FILES_${PN} += "${libdir}/tcl${VER} ${libdir}/tcl8.6 ${libdir}/tcl8"
FILES_${PN}-dev += "${libdir}/tclConfig.sh ${libdir}/tclooConfig.sh"

ALTERNATIVE_${PN}-doc = "Thread.3"
ALTERNATIVE_LINK_NAME[Thread.3] = "${mandir}/man3/Thread.3"

# isn't getting picked up by shlibs code
RDEPENDS_${PN} += "tcl-lib"
RDEPENDS_${PN}-ptest += "libgcc"

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
	oe_runmake tcltest
}

do_install_ptest() {
	cp ${B}/tcltest ${D}${PTEST_PATH}
	cp -r ${S}/../library ${D}${PTEST_PATH}
	cp -r ${S}/../tests ${D}${PTEST_PATH}
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
