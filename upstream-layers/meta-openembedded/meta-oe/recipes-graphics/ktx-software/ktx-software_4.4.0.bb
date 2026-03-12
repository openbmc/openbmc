SUMMARY = "KTX (Khronos Texture) Library and Tools "
DESCRIPTION = "KTX (Khronos Texture) is a lightweight container for textures for OpenGL, Vulkan and other GPU APIs."
HOMEPAGE = "https://github.com/KhronosGroup/KTX-Software"

LICENSE = "Apache-2.0 & BSD-1-Clause & BSD-2-Clause & BSD-3-Clause & BSL-1.0 \
	   & CC-BY-3.0 & CC-BY-4.0 & CC0-1.0 & Zlib & MIT & HI-Trademark & fmt & \
	   Kodak & PNGSuite & ETCSLA & Cesium-Trademark-Terms"

LIC_FILES_CHKSUM = "file://REUSE.toml;md5=45910de791d599e6ba472f6fa506f391"

SRC_URI = "git://github.com/KhronosGroup/KTX-Software.git;protocol=https;branch=main;lfs=0"
SRCREV = "beef80159525d9fb7abb8645ea85f4c4f6842e8f"

inherit cmake

# BASISU does not work with avx
TUNE_CCARGS:append:x86-64 = " -mno-avx"

PACKAGECONFIG[ocl_backend] = "-DBASISU_SUPPORT_OPENCL=ON, -DBASISU_SUPPORT_OPENCL=OFF, virtual/libopencl1"
