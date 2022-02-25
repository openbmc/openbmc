SUMMARY = "Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "The Device Tree Compiler is a tool used to manipulate the Open-Firmware-like device tree used by PowerPC kernels."
SECTION = "bootloader"
LICENSE = "GPL-2.0-only | BSD-2-Clause"

LIC_FILES_CHKSUM = "file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://libfdt/libfdt.h;beginline=4;endline=7;md5=05bb357cfb75cae7d2b01d2ee8d76407"

SRC_URI = "git://git.kernel.org/pub/scm/utils/dtc/dtc.git;branch=master \
           file://0001-fdtdump-fix-Werror-int-to-pointer-cast.patch"
SRCREV = "b6910bec11614980a21e46fbccc35934b671bd81"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dpython=disabled -Dvalgrind=disabled"

PACKAGECONFIG ??= "tools"
PACKAGECONFIG[tools] = "-Dtools=true,-Dtools=false,flex-native bison-native"
PACKAGECONFIG[yaml] = "-Dyaml=enabled,-Dyaml=disabled,libyaml"

PACKAGES =+ "${PN}-misc"
FILES:${PN}-misc = "${bindir}/convert-dtsv0 ${bindir}/ftdump ${bindir}/dtdiff"
RDEPENDS:${PN}-misc += "${@bb.utils.contains('PACKAGECONFIG', 'tools', 'bash diffutils', '', d)}"

BBCLASSEXTEND = "native nativesdk"
