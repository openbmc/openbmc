SUMMARY = "Utility for printing information EGL and its client APIs (OpenGL, OpenGL ES, OpenVG)"
SECTION = "graphics"
LICENSE = "Zlib"
HOMEPAGE = "https://github.com/dv1/eglinfo"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8d4f33bc3add976f7dfae85dab66f03c"

DEPENDS = "virtual/egl"

SRC_URI = "git://github.com/dv1/eglinfo.git;branch=master \
           file://0001-Add-STAGING_INCDIR-to-searchpath-for-egl-headers.patch \
          "
SRCREV = "4b317648ec6cf39556a9e5d8078f605bc0edd5de"

CVE_PRODUCT = "eglinfo"

S = "${WORKDIR}/git"

inherit waf distro_features_check
# depends on virtual/egl
REQUIRED_DISTRO_FEATURES ?= "opengl"

# BSP layers should add .bbappend files for the -x11 and -fb .bb recipes, which
# append the respective EGL/OpenGLES/OpenVG libraries to DEPENDS and set
# EGL_DEVICE as appropriate.
EGLINFO_DEVICE ?= "generic"

EXTRA_OECONF = "--platform=${EGLINFO_PLATFORM} \
                --device=${EGLINFO_DEVICE} \
                --sysroot ${STAGING_DIR_TARGET}"

do_install_append() {
	mv ${D}/${bindir}/eglinfo ${D}/${bindir}/${EGLINFO_BINARY_NAME}
}
