SUMMARY = "Fortran Hello World"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DEPENDS = "libgfortran"

SRC_URI = "file://hello.f95"

# These set flags that Fortran doesn't support
SECURITY_CFLAGS = ""
SECURITY_LDFLAGS = ""

do_compile() {
	${HOST_PREFIX}gfortran ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} ${LDFLAGS} ${WORKDIR}/hello.f95 -o ${B}/fortran-hello
}

do_install() {
	install -d ${D}${bindir}
	install ${B}/fortran-hello ${D}${bindir}
}

python () {
    if not d.getVar("FORTRAN"):
        raise bb.parse.SkipRecipe("Fortran isn't enabled")
}