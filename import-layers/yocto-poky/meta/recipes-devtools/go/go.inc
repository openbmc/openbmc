inherit goarch
DEPENDS += "go-bootstrap-native"

# libgcc is required for the target specific libraries to build
# properly, but apparently not for go-cross and, more importantly,
# also can't be used there because go-cross cannot depend on
# the tune-specific libgcc. Otherwise go-cross also would have
# to be tune-specific.
DEPENDS += "${@ 'libgcc' if not oe.utils.inherits(d, 'cross') else ''}"

# Prevent runstrip from running because you get errors when the host arch != target arch
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

# x32 ABI is not supported on go compiler so far
COMPATIBLE_HOST_linux-gnux32 = "null"
# ppc32 is not supported in go compilers
COMPATIBLE_HOST_powerpc = "null"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go1.4"
export GOROOT_FINAL = "${libdir}/go"
export CGO_ENABLED = "1"
export CC_FOR_TARGET = "${CC}"
export CXX_FOR_TARGET = "${CXX}"

do_configure[noexec] = "1"

do_compile_prepend_class-cross() {
	export CGO_ENABLED=0
}

do_compile() {
	export GOBIN="${B}/bin"
	export CC="${@d.getVar('BUILD_CC', True).strip()}"
	rm -rf ${GOBIN} ${B}/pkg
	mkdir ${GOBIN}

	export TMPDIR=${WORKDIR}/build-tmp
	mkdir -p ${WORKDIR}/build-tmp

	cd src
	./make.bash --host-only
	# Ensure cgo.a is built with the target toolchain
	export GOBIN="${B}/target/bin"
	rm -rf ${GOBIN}
	mkdir -p ${GOBIN}
	GO_FLAGS="-a" ./make.bash
}

do_install_class-target() {
	install -d ${D}${libdir}/go
	cp -a ${B}/pkg ${D}${libdir}/go/
	install -d ${D}${libdir}/go/src
	(cd ${S}/src; for d in *; do \
		[ -d $d ] && cp -a ${S}/src/$d ${D}${libdir}/go/src/; \
	done)
	install -d ${D}${bindir}
	if [ -d ${B}/bin/${GOOS}_${GOARCH} ]
	then
		install -m 0755 ${B}/bin/${GOOS}_${GOARCH}/* ${D}${bindir}
	else
		install -m 0755 ${B}/bin/* ${D}${bindir}
	fi
}

do_install_class-cross() {
	install -d ${D}${libdir}/go
	cp -a ${B}/pkg ${D}${libdir}/go/
	install -d ${D}${libdir}/go/src
	(cd ${S}/src; for d in *; do \
		[ -d $d ] && cp -a ${S}/src/$d ${D}${libdir}/go/src/; \
	done)
	install -d ${D}${bindir}
	for f in ${B}/bin/go*
	do
		install -m755 $f ${D}${bindir}
	done
}
do_package_qa[noexec] = "1"

RDEPENDS_${PN} += "perl"
