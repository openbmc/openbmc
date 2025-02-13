SUMMARY = "DMTF's Reference Implementation of SPDM"
DESCRIPTION = "libspdm is a reference implementation of the DMTF’s Security Protocols and \
Data Models (SPDM). This is used to enable authentication, attestation and key exchange to \
assist in providing infrastructure security enablement"
HOMEPAGE = "https://github.com/DMTF/libspdm"
BUGTRACKER = "https://github.com/DMTF/libspdm/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8f9b59a81a88da8e812af43728b72dd7"

DEPENDS = "openssl"

SRC_URI = "git://github.com/DMTF/libspdm.git;branch=main;protocol=https"
SRCREV = "5ebe5e3946b9439928fa3a7548268c29cccc1b16"

S = "${WORKDIR}/git"

inherit cmake

LIBSPDM_CFLAGS = "\
	${OECMAKE_C_FLAGS} \
	-DLIBSPDM_ENABLE_CAPABILITY_EVENT_CAP=0 \
	-DLIBSPDM_ENABLE_CAPABILITY_MEL_CAP=0 \
	-DLIBSPDM_ENABLE_CAPABILITY_GET_KEY_PAIR_INFO_CAP=0 \
	-DLIBSPDM_ENABLE_CAPABILITY_SET_KEY_PAIR_INFO_CAP=0 \
	-DLIBSPDM_HAL_PASS_SPDM_CONTEXT=1 \
	"

LIBSPDM_CFLAGS:append:toolchain-gcc = " -fPIE"

def get_spdm_multiarch(bb, d):
    target_arch = d.getVar('TARGET_ARCH')
    multiarch_options = {
        "x86_64":  "x64",
        "i586":    "ia32",
        "i686":    "ia32",
        "arm":     "arm",
        "aarch64": "aarch64",
        "riscv32": "riscv32",
        "riscv64": "riscv64",
        "ppc64le": "ppc64le",
    }

    if target_arch in multiarch_options :
        return multiarch_options[target_arch]

    bb.error("unsupported architecture '%s'" % target_arch)

EXTRA_OECMAKE += "\
    -DARCH=${@get_spdm_multiarch(bb, d)} \
	-DTOOLCHAIN=NONE \
	-DTARGET=Release \
	-DCRYPTO=openssl \
	-DENABLE_BINARY_BUILD=1 \
	-DCOMPILED_LIBCRYPTO_PATH=${libdir} \
	-DCOMPILED_LIBSSL_PATH=${libdir} \
	-DDISABLE_TESTS=1 \
	-DDISABLE_EDDSA=1 \
	-DCMAKE_C_FLAGS='${LIBSPDM_CFLAGS}' \
	"

do_install () {
    install -d ${D}/${libdir}
    install -m 0644 lib/* ${D}/${libdir}/

    install -d ${D}/${includedir}/${BPN}/
    cp -rf ${S}/include/* ${D}/${includedir}/${BPN}/

    install -d ${D}/${includedir}/${BPN}/os_stub/spdm_crypt_ext_lib
    cp -rf ${S}/os_stub/spdm_crypt_ext_lib/*.h ${D}/${includedir}/${BPN}/os_stub/spdm_crypt_ext_lib/
}

FILES:${PN} += "${libdir}/*.a"
FILES:${PN} += "${includedir}/${BPN}/*.h"
FILES:${PN} += "${includedir}/${BPN}/os_stub/spdm_crypt_ext_lib/*.h"

COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:powerpc64 = "null"
COMPATIBLE_HOST:mipsarchn32 = "null"
COMPATIBLE_HOST:mipsarcho32 = "null"

BBCLASSEXTEND = "native nativesdk"
