SUMMARY = "Shows and manipulates data structure layout"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "elfutils zlib libbpf"

COMPATIBLE_HOST = "(x86_64|i.86|aarch64).*-linux"

SRCREV = "f02af2553ea58ae1186226af0d0ec835a248358f"
SRC_URI = "git://git.kernel.org/pub/scm/devel/pahole/pahole.git \
           file://0001-CMakeList.txt-make-python-optional.patch"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG[python3] = ",,python3-core,python3-core"

EXTRA_OECMAKE = "-D__LIB=lib -DCMAKE_BUILD_TYPE=Release -DLIBBPF_EMBEDDED=OFF"

FILES:${PN} =  "${bindir}/pahole \
		${libdir}/libdwarves.so* \
		${libdir}/libdwarves_reorganize.so*"

PACKAGES += "${PN}-extra"
FILES:${PN}-extra = "${datadir} ${bindir} ${libdir}/libdwarves_emit.so*"
RDEPENDS:${PN}-extra += "bash python3-core"

BBCLASSEXTEND = "native nativesdk"
