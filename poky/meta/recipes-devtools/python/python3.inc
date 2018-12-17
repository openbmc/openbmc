SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"

# TODO Remove this when we upgrade
INC_PR = "r1"
PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b6ec515b22618f55fa07276b897bacea"

# TODO consolidate patch set
SRC_URI[md5sum] = "f5a99f765e765336a3ebbb2a24ca2be3"
SRC_URI[sha256sum] = "f55cde04f521f273c7cba08912921cc5642cfc15ca7b22d5829f0aff4371155f"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"

CVE_PRODUCT = "python"

PYTHON_MAJMIN = "3.5"
PYTHON_BINABI = "${PYTHON_MAJMIN}m"

S = "${WORKDIR}/Python-${PV}"

inherit autotools bluetooth pkgconfig

EXTRA_OECONF = "\
  --with-threads \
  --with-pymalloc \
  --without-cxx-main \
  --with-signal-module \
  --enable-shared \
  --enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)} \
"

PACKAGECONFIG[bluetooth] = ",ac_cv_header_bluetooth_bluetooth_h=no ac_cv_header_bluetooth_h=no,${BLUEZ}"

do_install_append () {
	sed -i -e 's:${HOSTTOOLS_DIR}/install:install:g' \
		-e 's:${HOSTTOOLS_DIR}/mkdir:mkdir:g' \
		${D}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py
}
