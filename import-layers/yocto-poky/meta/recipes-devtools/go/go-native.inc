# Use immediate assingment here to get the original (/usr/lib)
# instead of the one rewritten by native.bbclass.
nonstaging_libdir := "${libdir}"

inherit native

SRC_URI_append = " http://golang.org/dl/go1.4.3.src.tar.gz;name=bootstrap;subdir=go1.4"
SRC_URI[bootstrap.md5sum] = "dfb604511115dd402a77a553a5923a04"
SRC_URI[bootstrap.sha256sum] = "9947fc705b0b841b5938c48b22dc33e9647ec0752bae66e50278df4f23f64959"

export GOOS = "${BUILD_GOOS}"
export GOARCH = "${BUILD_GOARCH}"
CC = "${@d.getVar('BUILD_CC').strip()}"

export CGO_ENABLED = "1"

do_configure() {
    cd ${WORKDIR}/go1.4/go/src
    CGO_ENABLED=0 GOROOT=${WORKDIR}/go1.4/go ./make.bash
}

do_compile() {
	export GOBIN="${B}/bin"
	export GOROOT_FINAL="${nonstaging_libdir}/go"
	export GOROOT_BOOTSTRAP="${WORKDIR}/go1.4/go"
	rm -rf ${GOBIN}
	mkdir ${GOBIN}

	export TMPDIR=${WORKDIR}/build-tmp
	mkdir -p ${WORKDIR}/build-tmp

	cd src
	./make.bash --host-only
}

make_wrapper() {
	rm -f ${D}${bindir}/$2$3
	cat <<END >${D}${bindir}/$2$3
#!/bin/bash
here=\`dirname \$0\`
export GOROOT="${GOROOT:-\`readlink -f \$here/../lib/go\`}"
\$here/../lib/go/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$2
}

do_install() {
	install -d ${D}${libdir}/go
	cp --preserve=mode,timestamps -R ${B}/pkg ${D}${libdir}/go/
	install -d ${D}${libdir}/go/src
	(cd ${S}/src; for d in *; do \
		[ -d $d ] && cp -a ${S}/src/$d ${D}${libdir}/go/src/; \
	done)
	rm -rf ${D}${libdir}/go/src/runtime/pprof/testdata
	install -d ${D}${bindir} ${D}${libdir}/go/bin
	for f in ${B}/bin/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base $base
	done
}
