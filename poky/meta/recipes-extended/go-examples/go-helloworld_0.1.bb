SUMMARY = "This is a simple example recipe that cross-compiles a Go program."
SECTION = "examples"
HOMEPAGE = "https://golang.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI = "git://go.googlesource.com/example;branch=master;protocol=https"
SRCREV = "d9923f6970e9ba7e0d23aa9448ead71ea57235ae"
UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "golang.org/x/example"
GO_INSTALL = "${GO_IMPORT}/hello"

export GO111MODULE="off"

inherit go

# This is just to make clear where this example is
do_install:append() {
    mv ${D}${bindir}/hello ${D}${bindir}/${BPN}
}
