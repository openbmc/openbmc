DESCRIPTION = "Fake version of the texinfo utility suite"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d6bb62e73ca8b901d3f2e9d71542f4bb"
DEPENDS = ""
PV = "1.0"

SRC_URI = "file://template.py file://COPYING"

S = "${WORKDIR}"

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

inherit native

do_install_name() {
    FILENAME="${D}${bindir}/$1"
    # Using ln causes problems with rm_work
    cp -T "${S}/template.py" "$FILENAME"
    chmod +x $FILENAME
}

do_install() {
    mkdir -p "${D}${bindir}"
    for i in makeinfo pod2texi texi2dvi pdftexi2dvi texindex texi2pdf \
             txixml2texi texi2any install-info ginstall-info \
             update-info-dir; do
        do_install_name $i
    done
}
