SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"
# bump this on every change in contrib/python/generate-manifest-2.7.py
INC_PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f257cc14f81685691652a3d3e1b5d754"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
           file://0001-bpo-33570-TLS-1.3-ciphers-for-OpenSSL-1.1.1-GH-6976-.patch \
           file://0002-bpo-34818-Add-missing-closing-wrapper-in-test_tls1_3.patch \
           file://0003-bpo-34834-Fix-test_ssl.test_options-to-account-for-O.patch \
           file://0004-bpo-34836-fix-test_default_ecdh_curve-needs-no-tlsv1.patch \
           file://0001-bpo-33354-Fix-test_ssl-when-a-filename-cannot-be-enc.patch \
           "

SRC_URI[md5sum] = "a80ae3cc478460b922242f43a1b4094d"
SRC_URI[sha256sum] = "22d9b1ac5b26135ad2b8c2901a9413537e08749a753356ee913c84dbd2df5574"

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
  ${PYTHONLSBOPTS} \
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
