SUMMARY = "Python package for parsing and generating vCard and vCalendar files"
SECTION = "devel/python" 
LICENSE = "Apache-2.0" 
LIC_FILES_CHKSUM = "file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"
HOMEPAGE = "http://vobject.skyhouseconsulting.com/" 
SRCNAME = "vobject" 
RDEPENDS_${PN} = "python python-dateutil"
PR = "r4"

SRC_URI = "http://vobject.skyhouseconsulting.com/${SRCNAME}-${PV}.tar.gz" 
S = "${WORKDIR}/${SRCNAME}-${PV}" 

inherit setuptools  

SRC_URI[md5sum] = "c9686dd74d39fdae140890d9c694c076"
SRC_URI[sha256sum] = "594113117f2017ed837c8f3ce727616f9053baa5a5463a7420c8249b8fc556f5"
