SUMMARY  = "Xilinx Runtime(XRT) libraries"
DESCRIPTION = "Xilinx Runtime User Space Libraries and headers"

LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=fa343562af4b9b922b8d7fe7b0b6d000 \
                    file://runtime_src/driver/xclng/drm/xocl/LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://runtime_src/driver/xclng/xrt/user_gem/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://runtime_src/driver/xclng/tools/xbutil/LICENSE;md5=d273d63619c9aeaf15cdaf76422c4f87"

SRC_URI = "git://github.com/Xilinx/XRT.git;protocol=https;nobranch=1"

PV = "2.2.0+git${SRCPV}"
SRCREV = "da87ac894a037d7e11c0496361458efed4bab438"

S = "${WORKDIR}/git/src"

inherit cmake

BBCLASSEXTEND = "native nativesdk"

# util-linux is for libuuid-dev.
DEPENDS = "libdrm opencl-headers ocl-icd opencl-clhpp boost util-linux git-replacement-native"
RDEPENDS_${PN} = "bash ocl-icd boost-system boost-filesystem"

EXTRA_OECMAKE += " \
		-DCMAKE_BUILD_TYPE=Release \
		-DCMAKE_EXPORT_COMPILE_COMANDS=ON \
		"
