SUMMARY = "Fortran Hello World"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DEPENDS = "libgfortran"

SRC_URI = "file://hello.f95"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

# These set flags that Fortran doesn't support
SECURITY_CFLAGS = ""
SECURITY_LDFLAGS = ""

do_compile() {
	${FC} ${LDFLAGS} hello.f95 -o ${B}/fortran-hello
}

do_install() {
	install -D ${B}/fortran-hello ${D}${bindir}/fortran-hello
}

python () {
    if not d.getVar("FORTRAN"):
        raise bb.parse.SkipRecipe("Fortran isn't enabled")
}
