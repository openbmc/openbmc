inherit native

BOOTSTRAP ?= ""
export GOOS = "${BUILD_GOOS}"
export GOARCH = "${BUILD_GOARCH}"
export GOROOT_FINAL = "${STAGING_LIBDIR_NATIVE}/go${BOOTSTRAP}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go1.4"
export CGO_ENABLED = "1"

do_configure[noexec] = "1"

do_compile() {
	export GOBIN="${B}/bin"
	rm -rf ${GOBIN}
	mkdir ${GOBIN}

	export TMPDIR=${WORKDIR}/build-tmp
	mkdir -p ${WORKDIR}/build-tmp

	cd src
	CGO_ENABLED=0 ./make.bash --host-only
}

make_wrapper() {
	rm -f ${D}${bindir}/$2$3
	cat <<END >${D}${bindir}/$2$3
#!/bin/bash
here=\`dirname \$0\`
export GOROOT="${GOROOT:-\`readlink -f \$here/../lib/go$3\`}"
\$here/../lib/go$3/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$2$3
}

do_install() {
	install -d ${D}${libdir}/go${BOOTSTRAP}
	cp -a ${B}/pkg ${D}${libdir}/go${BOOTSTRAP}/
	install -d ${D}${libdir}/go${BOOTSTRAP}/src
	(cd ${S}/src; for d in *; do \
		[ -d $d ] && cp -a ${S}/src/$d ${D}${libdir}/go${BOOTSTRAP}/src/; \
	done)

	install -d ${D}${bindir} ${D}${libdir}/go${BOOTSTRAP}/bin
	for f in ${B}/bin/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go${BOOTSTRAP}/bin
		make_wrapper $base $base ${BOOTSTRAP}
	done
}

do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
