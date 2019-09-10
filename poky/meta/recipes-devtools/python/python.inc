SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"
# bump this on every change in contrib/python/generate-manifest-2.7.py
INC_PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e466242989bd33c1bd2b6a526a742498"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
           file://bpo-35907-cve-2019-9948.patch \
           file://bpo-35907-cve-2019-9948-fix.patch \
           file://bpo-36216-cve-2019-9636.patch \
           file://bpo-36216-cve-2019-9636-fix.patch \
           file://CVE-2019-9740.patch \
           file://CVE-2018-20852.patch \
           "

SRC_URI[md5sum] = "30157d85a2c0479c09ea2cbe61f2aaf5"
SRC_URI[sha256sum] = "f222ef602647eecb6853681156d32de4450a2c39f4de93bd5b20235f2e660ed7"

# python recipe is actually python 2.x
# also, exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>2(\.\d+)+).tar"

CVE_PRODUCT = "python"

PYTHON_MAJMIN = "2.7"

inherit autotools pkgconfig

EXTRA_OECONF = "\
  --with-threads \
  --with-pymalloc \
  --without-cxx-main \
  --with-signal-module \
  --enable-shared \
  --enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)} \
  ac_cv_header_bluetooth_bluetooth_h=no ac_cv_header_bluetooth_h=no \
"

do_configure_prepend() {
	libdirleaf="$(echo ${libdir} | sed -e 's:${prefix}/::')"
	sed -i -e "s:SEDMELIBLEAF:${libdirleaf}:g" \
		${S}/configure.ac
}

do_install_append () {
	sed -i -e 's:${HOSTTOOLS_DIR}/install:install:g' \
		-e 's:${HOSTTOOLS_DIR}/mkdir:mkdir:g' \
		${D}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py
}
