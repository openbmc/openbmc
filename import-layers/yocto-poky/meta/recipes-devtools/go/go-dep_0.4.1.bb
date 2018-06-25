SUMMARY = "Dependency management tool for Golang"
HOMEPAGE = "https://github.com/golang/dep"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1bad315647751fab0007812f70d42c0d"

GO_IMPORT = "github.com/golang/dep"
SRC_URI = "git://${GO_IMPORT} \
           file://0001-Add-support-for-mips-mips64.patch;patchdir=src/github.com/golang/dep \
          "

# Points to 0.4.1 tag
SRCREV = "37d9ea0ac16f0e0a05afc3b60e1ac8c364b6c329"

inherit go

GO_INSTALL = "${GO_IMPORT}/cmd/dep"

RDEPENDS_${PN}-dev += "bash"

BBCLASSEXTEND = "native nativesdk"
