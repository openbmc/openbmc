SUMMARY = "Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "The Device Tree Compiler is a toolchain for working with device tree source and binary files."
SECTION = "bootloader"
LICENSE = "GPL-2.0-only | BSD-2-Clause"

LIC_FILES_CHKSUM = "file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://BSD-2-Clause;md5=5d6306d1b08f8df623178dfd81880927 \
                    file://README.license;md5=a5696bd07fcc7285cbbacc42c2132248 \
                    "

SRC_URI = " \
    git://git.kernel.org/pub/scm/utils/dtc/dtc.git;branch=main;protocol=https \
"
SRCREV = "8f48565e5cfedc74d3f7512f1e0188e9d85dc1de"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dpython=disabled -Dvalgrind=disabled"

PACKAGECONFIG ??= "tools"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,"
PACKAGECONFIG[tools] = "-Dtools=true,-Dtools=false,flex-native bison-native"
PACKAGECONFIG[yaml] = "-Dyaml=enabled,-Dyaml=disabled,libyaml"

PACKAGES =+ "${PN}-misc"
FILES:${PN}-misc = "${bindir}/convert-dtsv0 ${bindir}/ftdump ${bindir}/dtdiff"
RDEPENDS:${PN}-misc += "${@bb.utils.contains('PACKAGECONFIG', 'tools', 'bash diffutils', '', d)}"

BBCLASSEXTEND = "native nativesdk"
