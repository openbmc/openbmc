SUMMARY = "Dependency management tool for Golang"
HOMEPAGE = "https://github.com/golang/dep"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1bad315647751fab0007812f70d42c0d"

GO_IMPORT = "github.com/golang/dep"
SRC_URI = "git://${GO_IMPORT}"

# Points to 0.3.0 tag
SRCREV = "7a91b794bbfbf1f3b8b79823799316451127801b"

inherit go

GO_INSTALL = "${GO_IMPORT}/cmd/dep"

RDEPENDS_${PN}-dev += "bash"
