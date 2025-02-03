SUMMARY = "OpenCL CTS"
DESCRIPTION = "OpenCL CTS test suite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pkgconfig cmake

DEPENDS += "opencl-headers opencl-icd-loader"
RDEPENDS:${PN} += "python3-core python3-io"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CTS.git;protocol=https;branch=main;lfs=0 \
	   file://0001-Ignore-Compiler-Warnings.patch"

SRCREV = "a406b340913f622da089b00f284a597656c10239"

EXTRA_OECMAKE:append = " -DENABLE_WERROR=OFF -DCL_INCLUDE_DIR=${STAGING_INCDIR} -DCL_LIB_DIR=${STAGING_LIBDIR} -DOPENCL_LIBRARIES=OpenCL"

SECURITY_STRINGFORMAT:remove = "-Werror=format-security"

do_install() {
        install -d ${D}${bindir}/opencl_test_conformance
        cp -r ${B}/test_conformance/* ${D}${bindir}/opencl_test_conformance
        sed -i 's:/usr/bin/python:/usr/bin/python3:g' ${D}${bindir}/opencl_test_conformance/run_conformance.py
	 find "${D}${bindir}/opencl_test_conformance" -name cmake_install.cmake -type f -delete
        find "${D}${bindir}/opencl_test_conformance" -name CMakeFiles -type d -exec rm -rf "{}" \; -depth
}

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
