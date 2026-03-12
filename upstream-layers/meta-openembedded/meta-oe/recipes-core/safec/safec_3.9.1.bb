SUMMARY  = "Safe C Library"

LICENSE  = "safec"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d0eb7dfc57806a006fcbc4e389cf164"
SECTION = "lib"

inherit autotools pkgconfig

SRCREV = "39a0a819f80853498e48a6e601a446a122b64aaa"
SRC_URI = "git://github.com/rurban/safeclib.git;branch=master;protocol=https;tag=v${PV} \
           file://0001-vsnprintf_s-Increase-Buffer-Size-by-1.patch \
		  "
# arm-yoe-linux-gnueabi-clang: error: argument unused during compilation: '-mretpoline' [-Werror,-Wunused-command-line-argument]
# arm-yoe-linux-gnueabi-clang: error: argument unused during compilation: '-fstack-clash-protection' [-Werror,-Wunused-command-line-argument]
TUNE_CCARGS:append:toolchain-clang = " -Qunused-arguments"

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|powerpc64|arm|aarch64|mips).*-linux'

PACKAGES =+ "${PN}-check"

FILES:${PN}-check += "${bindir}/check_for_unsafe_apis"

RDEPENDS:${PN}-check += "perl"
