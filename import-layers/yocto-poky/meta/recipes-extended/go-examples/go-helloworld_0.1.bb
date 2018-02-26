DESCRIPTION = "This is a simple example recipe that cross-compiles a Go program."
SECTION = "examples"
HOMEPAGE = "https://golang.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://${GO_IMPORT}"
SRCREV = "46695d81d1fae905a270fb7db8a4d11a334562fe"

GO_IMPORT = "github.com/golang/example"
GO_INSTALL = "${GO_IMPORT}/hello"

inherit go

# This is just to make clear where this example is
do_install_append() {
    mv ${D}${bindir}/hello ${D}${bindir}/${BPN}
}
