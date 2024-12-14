SUMMARY = "Vulkan/GL/GLES/EGL/GLX/WGL Loader-Generator based on the official specifications for multiple languages."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae570f26774ac096cff8f992091a223c"

SRC_URI = "git://github.com/Dav1dde/glad.git;protocol=https;branch=glad2"
SRCREV = "73db193f853e2ee079bf3ca8a64aa2eaf6459043"
S = "${WORKDIR}/git"

inherit python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
RDEPENDS:${PN} = "python3 python3-jinja2"
