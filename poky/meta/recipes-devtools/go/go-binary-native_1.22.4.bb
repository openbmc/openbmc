# This recipe is for bootstrapping our go-cross from a prebuilt binary of Go from golang.org.

SUMMARY = "Go programming language compiler (upstream binary for bootstrap)"
HOMEPAGE = " http://golang.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

PROVIDES = "go-native"

# Checksums available at https://go.dev/dl/
SRC_URI = "https://dl.google.com/go/go${PV}.${BUILD_GOOS}-${BUILD_GOARCH}.tar.gz;name=go_${BUILD_GOTUPLE}"
SRC_URI[go_linux_amd64.sha256sum] = "ba79d4526102575196273416239cca418a651e049c2b099f3159db85e7bade7d"
SRC_URI[go_linux_arm64.sha256sum] = "a8e177c354d2e4a1b61020aca3562e27ea3e8f8247eca3170e3fa1e0c2f9e771"
SRC_URI[go_linux_ppc64le.sha256sum] = "a3e5834657ef92523f570f798fed42f1f87bc18222a16815ec76b84169649ec4"

UPSTREAM_CHECK_URI = "https://golang.org/dl/"
UPSTREAM_CHECK_REGEX = "go(?P<pver>\d+(\.\d+)+)\.linux"

CVE_PRODUCT = "golang:go"

S = "${WORKDIR}/go"

inherit goarch native

do_compile() {
    :
}

make_wrapper() {
	rm -f ${D}${bindir}/$1
	cat <<END >${D}${bindir}/$1
#!/bin/bash
here=\`dirname \$0\`
export GOROOT="${GOROOT:-\`readlink -f \$here/../lib/go\`}"
\$here/../lib/go/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$1
}

do_install() {
    find ${S} -depth -type d -name testdata -exec rm -rf {} +

	install -d ${D}${bindir} ${D}${libdir}/go
	cp --preserve=mode,timestamps -R ${S}/ ${D}${libdir}/

	for f in ${S}/bin/*
	do
	  	make_wrapper `basename $f`
	done
}
