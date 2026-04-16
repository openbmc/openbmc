SUMMARY = "Public domain mDNS/DNS-SD library in C"
DESCRIPTION = "This library provides a header only cross-platform mDNS and DNS-DS library in C"
SECTION = "net"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=911690f51af322440237a253d695d19f"

SRC_URI = "git://github.com/mjansson/mdns.git;protocol=https;branch=main;tag=${PV} \
    file://0001-Bump-cmake_minimum_required-version-to-3.5-94.patch \
"
SRCREV = "1727be0602941a714cb6048a737f0584b1cebf3c"

inherit cmake

CVE_PRODUCT = "mjansson:mdns"
