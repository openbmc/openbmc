# This recipe is for bootstrapping our go-cross from a prebuilt binary of Go from golang.org.

SUMMARY = "Go programming language compiler (upstream binary for bootstrap)"
HOMEPAGE = " http://golang.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7998cb338f82d15c0eff93b7004d272a"

PROVIDES = "go-native"

# Checksums available at https://go.dev/dl/
SRC_URI = "https://dl.google.com/go/go${PV}.${BUILD_GOOS}-${BUILD_GOARCH}.tar.gz;name=go_${BUILD_GOTUPLE}"
SRC_URI[go_linux_amd64.sha256sum] = "5c2c3b16caefa1d968a94c1daca04a7ca301a496d9b086e17ad77bb81393f053"
SRC_URI[go_linux_arm64.sha256sum] = "fe4789e92b1f33358680864bbe8704289e7bb5fc207d80623c308935bd696d49"
SRC_URI[go_linux_ppc64le.sha256sum] = "c5d60e2b303bb612f20cd82786594b64874e73b35134025e27d3390bf284ae43"

UPSTREAM_CHECK_URI = "https://golang.org/dl/"
UPSTREAM_CHECK_REGEX = "go(?P<pver>\d+(\.\d+)+)\.linux"

CVE_PRODUCT = "golang:go"
CVE_STATUS[CVE-2024-3566] = "not-applicable-platform: Issue only applies on Windows"
CVE_STATUS[CVE-2024-24786] = "cpe-incorrect: this CVE is for golang-google-protobuf"

S = "${UNPACKDIR}/go"

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
