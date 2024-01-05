SUMMARY = "Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "The Device Tree Compiler is a toolchain for working with device tree source and binary files."
SECTION = "bootloader"
LICENSE = "GPL-2.0-only | BSD-2-Clause"

LIC_FILES_CHKSUM = "file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://BSD-2-Clause;md5=5d6306d1b08f8df623178dfd81880927 \
                    file://README.license;md5=a1eb22e37f09df5b5511b8a278992d0e"

SRC_URI = " \
    git://git.kernel.org/pub/scm/utils/dtc/dtc.git;branch=main;protocol=https \
    file://0001-meson.build-bump-version-to-1.7.0.patch \
    file://0002-meson-allow-building-from-shallow-clones.patch \
"
SRCREV = "039a99414e778332d8f9c04cbd3072e1dcc62798"

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
