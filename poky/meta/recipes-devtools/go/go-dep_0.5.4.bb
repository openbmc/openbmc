SUMMARY = "Dependency management tool for Golang"
HOMEPAGE = "https://github.com/golang/dep"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1bad315647751fab0007812f70d42c0d"

GO_IMPORT = "github.com/golang/dep"
SRC_URI = "git://${GO_IMPORT} \
           file://0001-Add-support-for-mips-mips64.patch;patchdir=src/github.com/golang/dep \
          "

SRCREV = "1f7c19e5f52f49ffb9f956f64c010be14683468b"

inherit go

GO_INSTALL = "${GO_IMPORT}/cmd/dep"

RDEPENDS_${PN}-dev += "bash"

BBCLASSEXTEND = "native nativesdk"

# For compiling ptest on mips and mips64, the current go-dep version fails with the go 1.11 toolchain.
# error message: vet config not found
PTEST_ENABLED_mips = "0"
PTEST_ENABLED_mips64 = "0"
