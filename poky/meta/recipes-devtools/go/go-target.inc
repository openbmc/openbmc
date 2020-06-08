DEPENDS = "virtual/${TUNE_PKGARCH}-go go-native"
DEPENDS_class-nativesdk = "virtual/${TARGET_PREFIX}go-crosssdk go-native"

export GOCACHE = "${B}/.cache"
GO_LDFLAGS = ""
GO_LDFLAGS_class-nativesdk = "-linkmode external"
export GO_LDFLAGS

CC_append_class-nativesdk = " ${SECURITY_NOPIE_CFLAGS}"

do_configure[noexec] = "1"

do_compile() {
	export CC_FOR_${TARGET_GOOS}_${TARGET_GOARCH}="${CC}"
	export CXX_FOR_${TARGET_GOOS}_${TARGET_GOARCH}="${CXX}"

	cd src
	./make.bash --target-only --no-banner
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin ${B}/pkg"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"

do_install() {
	install -d ${D}${libdir}/go/pkg/tool
	cp --preserve=mode,timestamps -R ${B}/pkg/tool/${TARGET_GOTUPLE} ${D}${libdir}/go/pkg/tool/
	install -d ${D}${libdir}/go/src
	cp --preserve=mode,timestamps -R ${S}/src/cmd ${D}${libdir}/go/src/
	find ${D}${libdir}/go/src -depth -type d -name testdata -exec rm -rf {} \;
	install -d ${D}${libdir}/go/bin
	install -d ${D}${bindir}
	for f in ${B}/${GO_BUILD_BINDIR}/*; do
		name=`basename $f`
		install -m 0755 $f ${D}${libdir}/go/bin/
		ln -sf ../${baselib}/go/bin/$name ${D}${bindir}/
	done
	rm -rf ${D}${libdir}/go/src
}

PACKAGES = "${PN} ${PN}-dev"
FILES_${PN} = "${libdir}/go/bin ${libdir}/go/pkg/tool/${TARGET_GOTUPLE} ${bindir}"
RDEPENDS_${PN} = "go-runtime"
INSANE_SKIP_${PN} = "ldflags"

BBCLASSEXTEND = "nativesdk"
