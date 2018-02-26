inherit goarch
DEPENDS = "virtual/${TARGET_PREFIX}go go-native"
DEPENDS_class-nativesdk = "virtual/${TARGET_PREFIX}go-crosssdk go-native"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GO386 = "${TARGET_GO386}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"
export CGO_ENABLED = "1"
export CC_FOR_TARGET = "${CC}"
export CXX_FOR_TARGET = "${CXX}"
export GO_TARGET_INSTALL = "cmd"
export GO_FLAGS = "-a"
GO_LDFLAGS = ""
GO_LDFLAGS_class-nativesdk = "-linkmode external"
export GO_LDFLAGS

SECURITY_CFLAGS = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS = ""

do_configure[noexec] = "1"

do_compile() {
	export GOBIN="${B}/bin"
	export CC="${@d.getVar('BUILD_CC').strip()}"
	rm -rf ${GOBIN} ${B}/pkg
	mkdir ${GOBIN}

	export TMPDIR=${WORKDIR}/build-tmp
	mkdir -p ${WORKDIR}/build-tmp

	cd src
	./make.bash
	cd ${B}
}

do_install() {
	install -d ${D}${libdir}/go/pkg/tool
	cp --preserve=mode,timestamps -R ${B}/pkg/tool/${TARGET_GOTUPLE} ${D}${libdir}/go/pkg/tool/
	install -d ${D}${libdir}/go/src
	cp --preserve=mode,timestamps -R ${S}/src/cmd ${D}${libdir}/go/src/
	install -d ${D}${libdir}/go/bin
	install -d ${D}${bindir}
	for f in ${B}/${GO_BUILD_BINDIR}/*; do
		name=`basename $f`
		install -m 0755 $f ${D}${libdir}/go/bin/
		ln -sf ../${BASELIB}/go/bin/$name ${D}${bindir}/
	done
}

PACKAGES = "${PN} ${PN}-dev"
FILES_${PN} = "${libdir}/go/bin ${libdir}/go/pkg/tool/${TARGET_GOTUPLE} ${bindir}"
FILES_${PN}-dev = "${libdir}/go"
RDEPENDS_${PN}-dev = "perl bash"
INSANE_SKIP_${PN} = "ldflags"

BBCLASSEXTEND = "nativesdk"
