inherit native

SRC_URI_append = " https://dl.google.com/go/go1.4-bootstrap-20171003.tar.gz;name=bootstrap;subdir=go1.4"
SRC_URI[bootstrap.md5sum] = "dbf727a4b0e365bf88d97cbfde590016"
SRC_URI[bootstrap.sha256sum] = "f4ff5b5eb3a3cae1c993723f3eab519c5bae18866b5e5f96fe1102f0cb5c3e52"

export GOOS = "${BUILD_GOOS}"
export GOARCH = "${BUILD_GOARCH}"
CC = "${@d.getVar('BUILD_CC').strip()}"

GOMAKEARGS ?= "--no-banner"

do_configure() {
	cd ${WORKDIR}/go1.4/go/src
	CGO_ENABLED=0 GOROOT=${WORKDIR}/go1.4/go ./make.bash
}

do_compile() {
	export GOROOT_FINAL="${libdir_native}/go"
	export GOROOT_BOOTSTRAP="${WORKDIR}/go1.4/go"

	cd src
	./make.bash ${GOMAKEARGS}
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin"

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
	find ${D}${libdir}/go/src -depth -type d -name testdata -exec rm -rf {} \;
	install -d ${D}${bindir} ${D}${libdir}/go/bin
	for f in ${B}/bin/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base $base
	done
}
