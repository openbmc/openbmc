SUMMARY = "This is a simple example recipe that cross-compiles a Go program."
SECTION = "examples"
HOMEPAGE = "https://golang.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a"

SRC_URI = "git://go.googlesource.com/example;branch=master;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"
SRCREV = "7f05d217867b2af52b0a28c6d1c91df97e1b5b39"
UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "golang.org/x/example"
GO_INSTALL = "${GO_IMPORT}/hello"

export GO111MODULE = "off"

inherit go

# This is just to make clear where this example is
do_install:append() {
    mv ${D}${bindir}/hello ${D}${bindir}/${BPN}
}

# /usr/lib/go/src/golang.org/x/example/ragserver/tests/weaviate-show-all.sh is requiring bash
RDEPENDS:${PN}-dev += "bash"
