SUMMARY = "libjpeg is a library for handling the JPEG (JFIF) image format"
DESCRIPTION = "libjpeg contains a library for handling the JPEG (JFIF) image format, as well as related programs for accessing the libjpeg functions."
HOMEPAGE = "http://www.ijg.org/"

LICENSE ="BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README;md5=ea93a8a2fed10106b63bc21679edacb9"

SECTION = "libs"

SRC_URI = "http://www.ijg.org/files/jpegsrc.v${PV}.tar.gz \
	  "

SRC_URI[md5sum] = "3353992aecaee1805ef4109aadd433e7"
SRC_URI[sha256sum] = "3a753ea48d917945dd54a2d97de388aa06ca2eb1066cbfdc6652036349fe05a7"

inherit autotools

PACKAGES =+ 		"jpeg-tools "
DESCRIPTION_jpeg-tools = "The jpeg-tools package includes the client programs for access libjpeg functionality.  These tools allow for the compression, decompression, transformation and display of JPEG files."
FILES_jpeg-tools = 	"${bindir}/*"

BBCLASSEXTEND = "native"

pkg_postinst_${PN}_linuxstdbase () {
    if [ ! -e $D${libdir}/libjpeg.so.62 ]; then
        JPEG=`find $D${libdir} -type f -name libjpeg.so.\*.\*.\*`
        ln -sf `basename $JPEG` $D${libdir}/libjpeg.so.62
    fi
}
