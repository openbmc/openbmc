SUMMARY = "ImageMagick is an image conversion toolkit"
SECTION = "console/utils"
HOMEPAGE = "https://www.imagemagick.org/"
DESCRIPTION = "ImageMagick is a collection of tools for displaying, converting, and \
editing raster and vector image files. It can read and write over 200 image file formats."
LICENSE = "ImageMagick"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1f56ade64cf079aff7232f7dbeaea992 \
                    file://NOTICE;md5=3974428a57d34b981abb7acc416dde8f"
# FIXME: There are many more checked libraries. All should be added or explicitly disabled to get consistent results.
DEPENDS = "lcms bzip2 jpeg libpng tiff zlib fftw freetype libtool"

BASE_PV = "${@d.getVar('PV').split('-')[0]}"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>([0-9][\.|_|-]?)+)"

SRC_URI = "git://github.com/ImageMagick/ImageMagick.git;branch=main;protocol=https;tag=${PV} \
           file://run-ptest \
           file://imagemagick-ptest.sh \
"

SRCREV = "d4e4b2b35a573fb7d96bc64ff1a417415bbe0f4c"

inherit autotools pkgconfig update-alternatives ptest
export ac_cv_sys_file_offset_bits = "64"

EXTRA_OECONF = "--program-prefix= --program-suffix=.im7 --without-perl --enable-largefile"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} cxx webp xml"
PACKAGECONFIG[cxx] = "--with-magick-plus-plus,--without-magick-plus-plus"
PACKAGECONFIG[graphviz] = "--with-gvc,--without-gvc,graphviz"
PACKAGECONFIG[heic] = "--with-heic,--without-heic,libheif"
PACKAGECONFIG[jxl] = "--with-jxl,--without-jxl,libjxl"
PACKAGECONFIG[lzma] = "--with-lzma,--without-lzma,xz"
PACKAGECONFIG[openjpeg] = "--with-openjp2,--without-openjp2,openjpeg"
PACKAGECONFIG[pango] = "--with-pango,--without-pango,pango cairo"
PACKAGECONFIG[raw] = "--with-raw,--without-raw,libraw"
PACKAGECONFIG[rsvg] = "--with-rsvg,--without-rsvg,librsvg"
PACKAGECONFIG[tcmalloc] = "--with-tcmalloc=yes,--with-tcmalloc=no,gperftools"
PACKAGECONFIG[webp] = "--with-webp,--without-webp,libwebp"
PACKAGECONFIG[wmf] = "--with-wmf,--without-wmf,libwmf"
PACKAGECONFIG[x11] = "--with-x,--without-x,virtual/libx11 libxext libxt"
PACKAGECONFIG[xml] = "--with-xml,--without-xml,libxml2"

do_install:append:class-target() {
    for file in MagickCore-config.im7 MagickWand-config.im7; do
        sed -i 's,${STAGING_DIR_TARGET},,g' "${D}${bindir}/$file"
    done

    if ${@bb.utils.contains('PACKAGECONFIG', 'cxx', 'true', 'false', d)}; then
        sed -i 's,${STAGING_DIR_TARGET},,g' "${D}${bindir}/Magick++-config.im7"
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'xml', 'true', 'false', d)}; then
        xml_config="${D}${libdir}/ImageMagick-${BASE_PV}/config-Q16HDRI/configure.xml"
        sed -i 's,${S},,g' "$xml_config"
        sed -i 's,${B},,g' "$xml_config"
        sed -i 's,${RECIPE_SYSROOT},,g' "$xml_config"
    fi

    if ${@bb.utils.contains_any('PACKAGECONFIG', 'webp openjpeg', 'true', 'false', d)}; then
        sed -i 's,${HOSTTOOLS_DIR},${bindir},g' "${D}${sysconfdir}/ImageMagick-7/delegates.xml"
    fi
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/
    install -m 0755 ${UNPACKDIR}/imagemagick-ptest.sh ${D}${PTEST_PATH}/
}


FILES:${PN} += "${libdir}/ImageMagick-${BASE_PV}/config-Q16* \
                ${datadir}/ImageMagick-7"

FILES:${PN}-dev += "${libdir}/ImageMagick-${BASE_PV}/modules-Q16/*/*.a"

FILES:${PN}-dbg += "${libdir}/ImageMagick-${BASE_PV}/modules-Q16/*/.debug/*"

BBCLASSEXTEND = "native nativesdk"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "animate compare composite conjure convert display \
    identify import magick-script mogrify montage stream magick"

ALTERNATIVE_TARGET[animate] = "${bindir}/animate.im7"
ALTERNATIVE_TARGET[compare] = "${bindir}/compare.im7"
ALTERNATIVE_TARGET[composite] = "${bindir}/composite.im7"
ALTERNATIVE_TARGET[conjure] = "${bindir}/conjure.im7"
ALTERNATIVE_TARGET[convert] = "${bindir}/convert.im7"
ALTERNATIVE_TARGET[display] = "${bindir}/display.im7"
ALTERNATIVE_TARGET[identify] = "${bindir}/identify.im7"
ALTERNATIVE_TARGET[import] = "${bindir}/import.im7"
ALTERNATIVE_TARGET[magick-script] = "${bindir}/magick-script.im7"
ALTERNATIVE_TARGET[mogrify] = "${bindir}/mogrify.im7"
ALTERNATIVE_TARGET[montage] = "${bindir}/montage.im7"
ALTERNATIVE_TARGET[stream] = "${bindir}/stream.im7"
ALTERNATIVE_TARGET[magick] = "${bindir}/magick.im7"

ALTERNATIVE:${PN}-doc = "animate.1 compare.1 composite.1 conjure.1 \
    convert.1 display.1 identify.1 import.1 magick-script.1 mogrify.1 montage.1 stream.1"

ALTERNATIVE_LINK_NAME[animate.1] = "${mandir}/man1/animate.1"
ALTERNATIVE_TARGET[animate.1] = "${mandir}/man1/animate.im7.1"
ALTERNATIVE_LINK_NAME[compare.1] = "${mandir}/man1/compare.1"
ALTERNATIVE_TARGET[compare.1] = "${mandir}/man1/compare.im7.1"
ALTERNATIVE_LINK_NAME[composite.1] = "${mandir}/man1/composite.1"
ALTERNATIVE_TARGET[composite.1] = "${mandir}/man1/composite.im7.1"
ALTERNATIVE_LINK_NAME[conjure.1] = "${mandir}/man1/conjure.1"
ALTERNATIVE_TARGET[conjure.1] = "${mandir}/man1/conjure.im7.1"
ALTERNATIVE_LINK_NAME[convert.1] = "${mandir}/man1/convert.1"
ALTERNATIVE_TARGET[convert.1] = "${mandir}/man1/convert.im7.1"
ALTERNATIVE_LINK_NAME[display.1] = "${mandir}/man1/display.1"
ALTERNATIVE_TARGET[display.1] = "${mandir}/man1/display.im7.1"
ALTERNATIVE_LINK_NAME[identify.1] = "${mandir}/man1/identify.1"
ALTERNATIVE_TARGET[identify.1] = "${mandir}/man1/identify.im7.1"
ALTERNATIVE_LINK_NAME[import.1] = "${mandir}/man1/import.1"
ALTERNATIVE_TARGET[import.1] = "${mandir}/man1/import.im7.1"
ALTERNATIVE_LINK_NAME[magick-script.1] = "${mandir}/man1/magick-script.1"
ALTERNATIVE_TARGET[magick-script.1] = "${mandir}/man1/magick-script.im7.1"
ALTERNATIVE_LINK_NAME[mogrify.1] = "${mandir}/man1/mogrify.1"
ALTERNATIVE_TARGET[mogrify.1] = "${mandir}/man1/mogrify.im7.1"
ALTERNATIVE_LINK_NAME[montage.1] = "${mandir}/man1/montage.1"
ALTERNATIVE_TARGET[montage.1] = "${mandir}/man1/montage.im7.1"
ALTERNATIVE_LINK_NAME[stream.1] = "${mandir}/man1/stream.1"
ALTERNATIVE_TARGET[stream.1] = "${mandir}/man1/stream.im7.1"
ALTERNATIVE_LINK_NAME[magick] = "${bindir}/magick"

CVE_STATUS[CVE-2014-9826] = "fixed-version: The current version (7.1.1) is not affected by the CVE which affects versions at least earlier than 6.9.4-0"
CVE_STATUS[CVE-2016-7538] = "fixed-version: The current version (7.1.1) is not affected by the CVE which affects versions at least earlier than 6.9.4-0"
CVE_STATUS[CVE-2017-5506] = "fixed-version: The current version (7.1.1) is not affected by the CVE which affects versions at least earlier than 7.0.4-4"
