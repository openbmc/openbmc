SUMMARY = "drgn (pronounced dragon) is a debugger with an emphasis on  \
programmability. drgn exposes the types and variables in a program for easy, \
expressive scripting in Python."
HOMEPAGE = "https://github.com/osandov/drgn"
LICENSE = " LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c83d30e99508d6b790eacdd3abab846"

SRC_URI = "git://github.com/osandov/drgn.git;protocol=https;branch=main"
SRCREV = "86b29d041c965030c24e9ab0a6907ff22c2a75fa"

S = "${WORKDIR}/git"

DEPENDS = "\
    autoconf-native \
    automake-native \
    libtool-native \
    elfutils \
    "

DEPENDS:append:toolchain-clang:class-target ="\
    openmp \
    "

OPENMP_LIB = "libgomp"
OPENMP_LIB:toolchain-clang:class-target = "openmp"

RDEPENDS:${PN} = "\
    python3-crypt \
    python3-io \
    python3-logging \
    python3-math \
    python3-pickle \
    python3-stringold \
    python3-compression \
    "

RDEPENDS:${PN}:append:class-target = "\
    libdw \
    libelf \
    ${OPENMP_LIB} \
    "

RDEPENDS:${PN}:append:class-native = "\
    elfutils-native \
    "

RDEPENDS:${PN}:append:class-nativesdk = "\
    nativesdk-elfutils \
    "

OPENMP_LIB_NAME = "gomp"
OPENMP_LIB_NAME:toolchain-clang:class-target = "omp"

export CONFIGURE_FLAGS = "\
    --build=${BUILD_SYS}, \
    --host=${HOST_SYS}, \
    --target=${TARGET_SYS}, \
    --prefix=${prefix}, \
    --exec_prefix=${exec_prefix}, \
    --bindir=${bindir}, \
    --sbindir=${sbindir}, \
    --libexecdir=${libexecdir}, \
    --datadir=${datadir}, \
    --sysconfdir=${sysconfdir}, \
    --sharedstatedir=${sharedstatedir}, \
    --localstatedir=${localstatedir}, \
    --libdir=${libdir}, \
    --includedir=${includedir}, \
    --oldincludedir=${includedir}, \
    --infodir=${infodir}, \
    --mandir=${mandir}, \
    --enable-openmp=${OPENMP_LIB_NAME} \
    "

export PYTHON_CPPFLAGS = "-I${STAGING_INCDIR}/${PYTHON_DIR}"

inherit python3native pkgconfig setuptools3

BBCLASSEXTEND = "native nativesdk"

