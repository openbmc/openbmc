SUMMARY = "7-zip is a commandline utility handling 7z archives."
HOMEPAGE = "http://www.7-zip.org/"
LICENSE = "LGPL-2.1-or-later & unRAR & PD & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://DOC/copying.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://DOC/unRarLicense.txt;md5=9c87ddde469ef94aed153b0951d088de \
                    file://DOC/License.txt;md5=44483e232b64ffca25fe001f3d6418d0"

CVE_PRODUCT = "7-zip 7zip"
SRC_URI = "git://github.com/ip7z/7zip.git;protocol=https;branch=main \
           file://0001-support-yocto-cross-compiling.patch \
           file://7z_wrapper.sh \
           "
SRCREV = "e5431fa6f5505e385c6f9367260717e9c47dc2ee"

UPSTREAM_CHECK_URI = "https://github.com/ip7z/7zip/releases/latest"

S = "${WORKDIR}/git"

# Support Yocto cross compiling
CXXFLAGS:append:toolchain-clang = " -Wno-error=cast-qual -Wno-error=sign-conversion \
                                    -Wno-error=disabled-macro-expansion \
                                    -Wno-error=shorten-64-to-32 \
                                    -Wno-error=cast-function-type-strict"

EXTRA_OEMAKE += " \
    CXXFLAGS_EXTRA='${CXXFLAGS}' \
    CFLAGS_BASE2='${CFLAGS}' \
    LDFLAGS_STATIC_3='${LDFLAGS}' \
"

# Support clang
MAKEFILE ?= "../../cmpl_gcc.mak"
MAKEFILE:class-target:toolchain-clang = "../../cmpl_clang.mak"

do_compile() {
    oe_runmake -C CPP/7zip/Bundles/Alone2    -f ${MAKEFILE}
    oe_runmake -C CPP/7zip/Bundles/Format7zF -f ${MAKEFILE}
    oe_runmake -C CPP/7zip/UI/Console        -f ${MAKEFILE}
    oe_runmake -C CPP/7zip/Bundles/SFXCon    -f ${MAKEFILE}
    oe_runmake -C CPP/7zip/Bundles/Alone     -f ${MAKEFILE}
    oe_runmake -C CPP/7zip/Bundles/Alone7z   -f ${MAKEFILE}
}

FILES:${PN} += "${libdir}/*"

FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

INSTALLDIR ?= "g"
INSTALLDIR:class-target:toolchain-clang = "c"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/CPP/7zip/Bundles/Alone/b/${INSTALLDIR}/7za ${D}${bindir}
	install -m 0755 ${S}/CPP/7zip/Bundles/Alone7z/b/${INSTALLDIR}/7zr ${D}${bindir}
	install -m 0755 ${S}/CPP/7zip/UI/Console/b/${INSTALLDIR}/7z ${D}${bindir}/7z.real
	install -m 0755 ${UNPACKDIR}/7z_wrapper.sh ${D}${bindir}/7z

	install -d ${D}${libdir}
	install -m 0755 ${S}/CPP/7zip/Bundles/Format7zF/b/${INSTALLDIR}/7z.so ${D}${libdir}/lib7z.so
	ln -rsn ${D}${libdir}/lib7z.so ${D}${bindir}/7z.so
}

PROVIDES += "p7zip"
RPROVIDES:${PN} += "lib7z.so()(64bit) 7z lib7z.so p7zip"
RPROVIDES:${PN}-dev += "lib7z.so()(64bit) 7z lib7z.so"

BBCLASSEXTEND = "native nativesdk"
