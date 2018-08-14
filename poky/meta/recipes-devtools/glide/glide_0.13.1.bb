SUMMARY = "Vendor Package Management for Golang"
HOMEPAGE = "https://glide.sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54905cf894f8cc416a92f4fc350c35b2"

GO_IMPORT = "github.com/Masterminds/glide"
SRC_URI = "git://${GO_IMPORT}"
SRCREV = "67790b3dbede72cfdc54aa53be4706322c9499e0"

inherit go

RDEPENDS_${PN}-dev += "bash"
RDEPENDS_${PN}-ptest += "bash"

BBCLASSEXTEND = "native nativesdk"
