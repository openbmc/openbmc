SUMMARY = "i.MX code signing tool"
DESCRIPTION = "Code signing support that integrates the HABv4 and AHAB library for i.MX processors"
LICENSE = "BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "\
    file://LICENSE.bsd3;md5=14aba05f9fa6c25527297c8aac95fcf6 \
    file://LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://LICENSE.openssl;md5=3441526b1df5cc01d812c7dfc218cea6 \
"

DEPENDS = "byacc-native flex-native openssl"

# debian: 3.4.0+dfsg-2
DEBIAN_PGK_NAME = "imx-code-signing-tool"
DEBIAN_PGK_VERSION = "${PV}+dfsg"

SRC_URI = "\
    ${DEBIAN_MIRROR}/main/i/${DEBIAN_PGK_NAME}/${DEBIAN_PGK_NAME}_${DEBIAN_PGK_VERSION}.orig.tar.xz \
    file://0001-fix-missing-makefile-rule-dependency.patch \
"

SRC_URI[sha256sum] = "342c0c028658a4a859fe70578b58c3b07e17bee0c7e3a13d063d4791e82c2dee"

S = "${UNPACKDIR}/${DEBIAN_PGK_NAME}-${DEBIAN_PGK_VERSION}"

EXTRA_OEMAKE = 'CC="${CC}" LD="${CC}" AR="${AR}" OBJCOPY="${OBJCOPY}"'

inherit siteinfo

do_compile() {
    oe_runmake -C code/obj.linux${SITEINFO_BITS} OSTYPE=linux${SITEINFO_BITS} ENCRYPTION=yes COPTIONS="${CFLAGS} ${CPPFLAGS}" LDOPTIONS="${LDFLAGS}"
    oe_runmake -C add-ons/hab_csf_parser COPTS="${CFLAGS} ${CPPFLAGS} ${LDFLAGS}"
}

do_install () {
    install -d ${D}${bindir}
    install -m 755 ${S}/code/obj.linux${SITEINFO_BITS}/cst ${D}${bindir}/
    install -m 755 ${S}/code/obj.linux${SITEINFO_BITS}/srktool ${D}${bindir}
    install -m 755 ${S}/add-ons/hab_csf_parser/csf_parser ${D}${bindir}
}

BBCLASSEXTEND = "native nativesdk"
