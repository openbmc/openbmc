SUMMARY = "KTX (Khronos Texture) Library and Tools "
DESCRIPTION = "KTX (Khronos Texture) is a lightweight container for textures for OpenGL, Vulkan and other GPU APIs."
HOMEPAGE = "https://github.com/KhronosGroup/KTX-Software"

LICENSE = " \
    Apache-2.0 AND BSD-1-Clause AND BSD-2-Clause AND BSD-3-Clause AND BSL-1.0 \
    AND CC-BY-3.0 AND CC-BY-4.0 AND CC0-1.0 AND MIT AND Zlib \
    AND LicenseRef-Cesium-Trademark-Terms AND LicenseRef-ETCSLA \
    AND LicenseRef-HI-Trademark AND LicenseRef-Kodak \
    AND LicenseRef-PNGSuite AND LicenseRef-fmt \
"

LIC_FILES_CHKSUM = "file://REUSE.toml;md5=9f87c97ba36aab42411bf93c094a61f2"

SRC_URI = "git://github.com/KhronosGroup/KTX-Software.git;protocol=https;branch=main;lfs=0"
SRCREV = "6269d2752ed04446c2d4749f54f3aad4f94555b5"

# Upstream tags are "v<ver>". PV 5.0.0 is really the v5.0.0-rc1 tag pinned
# above, and the checker skips rc/alpha/beta tags, so the newest resolvable
# release (4.4.2) is always lower than PV until 5.0.0 final is tagged.
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"
UPSTREAM_VERSION_UNKNOWN = "1"

inherit cmake

# BASISU does not work with avx
TUNE_CCARGS:append:x86-64 = " -mno-avx"

PACKAGECONFIG[ocl_backend] = "-DBASISU_SUPPORT_OPENCL=ON, -DBASISU_SUPPORT_OPENCL=OFF, virtual/libopencl1"
