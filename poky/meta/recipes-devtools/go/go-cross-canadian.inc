inherit cross-canadian

DEPENDS = "go-native virtual/${HOST_PREFIX}go virtual/nativesdk-${HOST_PREFIX}go-runtime \
           virtual/nativesdk-cross-cc virtual/nativesdk-libc \
           virtual/nativesdk-compilerlibs"
PN = "go-cross-canadian-${TRANSLATED_TARGET_ARCH}"

# it uses gcc on build machine during go-cross-canadian bootstrap, but
# the gcc version may be old and not support option '-fmacro-prefix-map'
# which is one of default values of DEBUG_PREFIX_MAP
DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                    "

export GOTOOLDIR_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/${HOST_SYS}/go/pkg/tool/${BUILD_GOTUPLE}"
export CGO_CFLAGS = "${CFLAGS}"
export CGO_LDFLAGS = "${LDFLAGS}"
export GO_LDFLAGS = '-extld ${HOST_PREFIX}gcc -extldflags "--sysroot=${STAGING_DIR_HOST} ${SECURITY_NOPIE_CFLAGS} ${HOST_CC_ARCH} ${LDFLAGS}"'

do_configure[noexec] = "1"

do_compile() {
	export CC_FOR_${HOST_GOTUPLE}="${HOST_PREFIX}gcc --sysroot=${STAGING_DIR_HOST} ${SECURITY_NOPIE_CFLAGS}"
	export CXX_FOR_${HOST_GOTUPLE}="${HOST_PREFIX}gxx --sysroot=${STAGING_DIR_HOST} ${SECURITY_NOPIE_CFLAGS}"
	cd src
	./make.bash --target-only --no-banner
	cd ${B}
}
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"


make_wrapper() {
	rm -f ${D}${bindir}/$2
	cat <<END >${D}${bindir}/$2
#!/bin/sh
here=\`dirname \$0\`
native_goroot=\`readlink -f \$here/../../lib/${TARGET_SYS}/go\`
export GOARCH="${TARGET_GOARCH}"
export GOOS="${TARGET_GOOS}"
test -n "\$GOARM" || export GOARM="${TARGET_GOARM}"
test -n "\$GO386" || export GO386="${TARGET_GO386}"
test -n "\$GOMIPS" || export GOMIPS="${TARGET_GOMIPS}"
export GOTOOLDIR="\$native_goroot/pkg/tool/${HOST_GOTUPLE}"
test -n "\$GOROOT" || export GOROOT="\$OECORE_TARGET_SYSROOT/${target_libdir}/go"
\$here/../../lib/${TARGET_SYS}/go/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$2
}

do_install() {
	install -d ${D}${libdir}/go/pkg/tool
	cp --preserve=mode,timestamps -R ${B}/pkg/tool/${HOST_GOTUPLE} ${D}${libdir}/go/pkg/tool/
	install -d ${D}${bindir} ${D}${libdir}/go/bin
	for f in ${B}/${GO_BUILD_BINDIR}/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base ${TARGET_PREFIX}$base
	done
}
