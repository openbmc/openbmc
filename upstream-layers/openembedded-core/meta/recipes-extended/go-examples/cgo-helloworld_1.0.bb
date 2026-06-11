SUMMARY = "A simple CGO example that calls C to print hello world"
SECTION = "examples"
HOMEPAGE = "https://golang.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://cgo-helloworld.go"

S = "${UNPACKDIR}"

GO_IMPORT = "cgo-helloworld"
GO_INSTALL = "${GO_IMPORT}"

inherit go

export GO111MODULE = "off"
export CGO_ENABLED = "1"

do_configure:prepend() {
    mkdir -p ${S}/src/${GO_IMPORT}
    cp ${UNPACKDIR}/cgo-helloworld.go ${S}/src/${GO_IMPORT}/main.go
}
