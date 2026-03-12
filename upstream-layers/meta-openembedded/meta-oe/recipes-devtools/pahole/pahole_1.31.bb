SUMMARY = "Shows and manipulates data structure layout"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "elfutils zlib libbpf"

# Depends on MACHINE_ARCH libbpf
PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "(x86_64|i.86|arm|aarch64|riscv64|powerpc|powerpc64|mips64).*-linux"

SRCREV = "1f2805b6eef104df3125143c949b391f6122e5b9"
SRC_URI = "git://git.kernel.org/pub/scm/devel/pahole/pahole.git;branch=master;tag=v${PV} \
           file://0001-Use-usr-bin-env-python3-instead-of-just-usr-bin-pyth.patch \
           "


inherit cmake pkgconfig

PACKAGECONFIG[python3] = ",,python3,python3-core"

EXTRA_OECMAKE = "-D__LIB=${@os.path.relpath(d.getVar('libdir'), d.getVar('prefix') + '/')} -DCMAKE_BUILD_TYPE=Release -DLIBBPF_EMBEDDED=OFF"

FILES:${PN} =  "${bindir}/pahole \
		${libdir}/libdwarves.so* \
		${libdir}/libdwarves_reorganize.so*"

PACKAGES += "${PN}-extra"
FILES:${PN}-extra = "${datadir} ${bindir} ${libdir}/libdwarves_emit.so*"
RDEPENDS:${PN}-extra += "bash python3-core"

BBCLASSEXTEND = "native nativesdk"
