SUMMARY = "Runtime typing introspection tools"
HOMEPAGE = "https://github.com/pydantic/typing-inspection"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dfe2d84c58973d6a532c4e7638dbb3d8"

DEPENDS = "python3-hatchling-native"

inherit pypi python_hatchling ptest-python-pytest
SRC_URI[sha256sum] = "7f16c411e7930dc79596d42ab44672a3fe5f60eb34b781a1647e7872363120c7"

RDEPENDS:${PN}-ptest += "python3-typing-extensions"

do_install_ptest:append() {
	# test_literal_values_unhashable_type asserts that duplicate unhashable
	# Literal values (e.g. Literal[[1, 'a'], [1, 'a']]) are preserved, but
	# CPython 3.13.15+ deduplicates unhashable Literal args at construction
	# time (https://github.com/python/cpython/pull/153914). Upstream fixed
	# this the same way starting in 0.4.4 by skipping the test on newer
	# Python; backport that same guard here since we're pinned to 0.4.2.
	sed -i \
		-e "/^def test_literal_values_unhashable_type/i @pytest.mark.skipif(sys.version_info >= (3, 13, 15), reason='Unhashable arguments are deduplicated (https://github.com/python/cpython/pull/153914)')" \
		${D}${PTEST_PATH}/tests/introspection/test_literal_values.py
}

BBCLASSEXTEND += "native nativesdk"
