SUMMARY = "The tdb library"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/LGPL-3.0;md5=bfccfe952269fff2b407dd11f2f3083b \
                    file://${COREBASE}/meta/files/common-licenses/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI = "http://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://do-not-check-xsltproc-manpages.patch"

SRC_URI[md5sum] = "fa4c9e2f59fcf41441285bca5f5ab481"
SRC_URI[sha256sum] = "0605ac0427eac9c23bf61ebfd8206a07d5ece198498eab1769cd0cfb6e7de6b1"

S = "${WORKDIR}/tdb-${PV}"

inherit waf-samba

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "
DISABLE_STATIC = ""

PACKAGES += "tdb-tools python-tdb python-tdb-dbg"

FILES_${PN} = "${libdir}/*.so.*"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_python-tdb-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug/*"
RDEPENDS_python-tdb = "python"
