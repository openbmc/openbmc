DESCRIPTION = "Evas generic loaders"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a87563ed30add2b0e3460272d3138a00"
DEPENDS = "eina poppler librsvg cairo"

inherit efl

SRCNAME = "evas_generic_loaders"

EXTRA_OECONF = " \
    --enable-poppler \
    --enable-svg \
    --disable-gstreamer \
    --disable-libraw \
    --disable-spectre \
"

PACKAGESPLITFUNCS_prepend = "split_evas_loader_packages "

python split_evas_loader_packages () {
    bb.note( "splitting packages for evas generic loaders" )
    basedir = d.expand( '${libdir}/evas/utils')

    do_split_packages(d, basedir, 'evas_image_loader\.(.*)$',
    output_pattern = "evas-generic-loader-%s",
    description = 'Evas generic loaders - module %s',
    allow_dirs=True, recursive=False, extra_depends="" )

    do_split_packages(d, basedir, 'evas_generic_pdf_loader\.(.*)$',
    output_pattern = "evas-generic-pdf-loader-%s",
    description = 'Evas generic pdf loaders - module %s',
    allow_dirs=True, recursive=False, extra_depends="" )
}

# add symlinks for different format (it doesn't make much sense to package them in separate packages)
FILES_evas-generic-loader-xcf += " \
    ${libdir}/evas/utils/evas_image_loader.xcf.gz \
"
FILES_evas-generic-loader-svg += " \
    ${libdir}/evas/utils/evas_image_loader.svg.gz \
    ${libdir}/evas/utils/evas_image_loader.svgz \
"
FILES_evas-generic-loader-pdf += " \
    ${libdir}/evas/utils/evas_image_loader.doc \
    ${libdir}/evas/utils/evas_image_loader.docx \
    ${libdir}/evas/utils/evas_image_loader.odp \
    ${libdir}/evas/utils/evas_image_loader.ods \
    ${libdir}/evas/utils/evas_image_loader.odt \
    ${libdir}/evas/utils/evas_image_loader.ppt \
    ${libdir}/evas/utils/evas_image_loader.pptx \
    ${libdir}/evas/utils/evas_image_loader.rtf \
    ${libdir}/evas/utils/evas_image_loader.xls \
    ${libdir}/evas/utils/evas_image_loader.xlsx \
"
FILES_evas-generic-pdf-loader-libreoffice += " \
    ${libdir}/evas/utils/evas_generic_pdf_loader.doc \
    ${libdir}/evas/utils/evas_generic_pdf_loader.docx \
    ${libdir}/evas/utils/evas_generic_pdf_loader.odp \
    ${libdir}/evas/utils/evas_generic_pdf_loader.ods \
    ${libdir}/evas/utils/evas_generic_pdf_loader.odt \
    ${libdir}/evas/utils/evas_generic_pdf_loader.ppt \
    ${libdir}/evas/utils/evas_generic_pdf_loader.pptx \
    ${libdir}/evas/utils/evas_generic_pdf_loader.rtf \
    ${libdir}/evas/utils/evas_generic_pdf_loader.xls \
    ${libdir}/evas/utils/evas_generic_pdf_loader.xlsx \
"

FILES_${PN}-dbg += "${libdir}/evas/utils/.debug"

PACKAGES += "evas-generic-loader-svg"
PACKAGES_DYNAMIC += "^evas-generic-loader-.* ^evas-generic-pdf-loader-.*"
