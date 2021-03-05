DESCRIPTION = "This is a simple example recipe that cross-compiles a Go program."
SECTION = "examples"
HOMEPAGE = "https://golang.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://${GO_IMPORT}"
SRCREV = "bcf50bfd7dcd8020c90965747d857ae42802e0c5"
UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "github.com/golang/example"
GO_INSTALL = "${GO_IMPORT}/hello"
GO_WORKDIR = "${GO_INSTALL}"

inherit go-mod

# This is just to make clear where this example is
do_install_append() {
    mv ${D}${bindir}/hello ${D}${bindir}/${BPN}
}
