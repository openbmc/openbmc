inherit cross

PROVIDES = "virtual/${TUNE_PKGARCH}-go"
DEPENDS = "go-native"

PN = "go-cross-${TUNE_PKGARCH}"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GO386 = "${TARGET_GO386}"
export GOMIPS = "${TARGET_GOMIPS}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"
export GOCACHE = "${B}/.cache"
CC = "${@d.getVar('BUILD_CC').strip()}"

do_configure[noexec] = "1"

do_compile() {
	export CC_FOR_${GOOS}_${GOARCH}="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
	export CXX_FOR_${GOOS}_${GOARCh}="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
	cd src
	./make.bash --host-only --no-banner
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin ${B}/pkg"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"

make_wrapper() {
	rm -f ${D}${bindir}/$2
	cat <<END >${D}${bindir}/$2
#!/bin/bash
here=\`dirname \$0\`
export GOARCH="${TARGET_GOARCH}"
export GOOS="${TARGET_GOOS}"
export GOARM="\${GOARM:-${TARGET_GOARM}}"
export GO386="\${GO386:-${TARGET_GO386}}"
export GOMIPS="\${GOMIPS:-${TARGET_GOMIPS}}"
\$here/../../lib/${CROSS_TARGET_SYS_DIR}/go/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$2
}

do_install() {
	install -d ${D}${libdir}/go
	cp --preserve=mode,timestamps -R ${B}/pkg ${D}${libdir}/go/
	install -d ${D}${libdir}/go/src
	(cd ${S}/src; for d in *; do \
		[ ! -d $d ] || cp --preserve=mode,timestamps -R ${S}/src/$d ${D}${libdir}/go/src/; \
	done)
	find ${D}${libdir}/go/src -depth -type d -name testdata -exec rm -rf {} \;
	install -d ${D}${bindir} ${D}${libdir}/go/bin
	for f in ${B}/bin/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base ${TARGET_PREFIX}$base
	done
}
