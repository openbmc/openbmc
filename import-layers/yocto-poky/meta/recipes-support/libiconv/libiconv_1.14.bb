SUMMARY = "Character encoding support library"
DESCRIPTION = "GNU libiconv - libiconv is for you if your application needs to support \
multiple character encodings, but that support lacks from your system."
HOMEPAGE = "http://www.gnu.org/software/libiconv"
SECTION = "libs"
NOTES = "Needs to be stripped down to: ascii iso8859-1 eucjp iso-2022jp gb utf8"
PROVIDES = "virtual/libiconv"
PR = "r1"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674 \
                    file://libcharset/COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
           file://autoconf.patch \
           file://add-relocatable-module.patch \
          "

SRC_URI[md5sum] = "e34509b1623cec449dfeb73d7ce9c6c6"
SRC_URI[sha256sum] = "72b24ded17d687193c3366d0ebe7cde1e6b18f0df8c55438ac95be39e8a30613"

S = "${WORKDIR}/libiconv-${PV}"

inherit autotools pkgconfig gettext

python __anonymous() {
    if d.getVar("TARGET_OS", True) != "linux":
        return
    if d.getVar("TCLIBC", True) == "glibc":
        raise bb.parse.SkipPackage("libiconv is provided for use with uClibc only - glibc already provides iconv")
}

EXTRA_OECONF += "--enable-shared --enable-static --enable-relocatable"

LEAD_SONAME = "libiconv.so"

do_configure_prepend () {
	rm -f ${S}/m4/libtool.m4 ${S}/m4/ltoptions.m4 ${S}/m4/ltsugar.m4 ${S}/m4/ltversion.m4 ${S}/m4/lt~obsolete.m4 ${S}/libcharset/m4/libtool.m4 ${S}/libcharset/m4/ltoptions.m4 ${S}/libcharset/m4/ltsugar.m4 ${S}/libcharset/m4/ltversion.m4 ${S}/libcharset/m4/lt~obsolete.m4
}

do_configure_append () {
        # forcibly remove RPATH from libtool
        sed -i 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' *libtool
        sed -i 's|^runpath_var=LD_RUN_PATH|runpath_var=_NO_RPATH_|g' *libtool
}

do_install_append () {
	rm -rf ${D}${libdir}/preloadable_libiconv.so
	rm -rf ${D}${libdir}/charset.alias
}

BBCLASSEXTEND = "nativesdk"
