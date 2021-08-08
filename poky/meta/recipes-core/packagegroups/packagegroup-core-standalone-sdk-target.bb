SUMMARY = "Target packages for the standalone SDK"
PR = "r8"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    libgcc \
    libgcc-dev \
    libatomic \
    libatomic-dev \
    libstdc++ \
    libstdc++-dev \
    ${LIBC_DEPENDENCIES} \
    "

RRECOMMENDS:${PN}:mingw32 = "\
    libssp \
    libssp-dev \
    "
