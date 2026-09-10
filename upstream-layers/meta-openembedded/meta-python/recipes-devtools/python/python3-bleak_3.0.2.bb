SUMMARY = "Bleak is a GATT client software, capable of connecting to BLE devices acting as GATT servers."
HOMEPAGE = "https://github.com/hbldh/bleak"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcbc2069a86cba1b5e47253679f66ed7"

# The PyPI sdist ships no tests/ directory at all, so ptest has nothing to
# run from it (pytest exits 5, "no tests collected"). Fetch the matching
# git tag instead, which does include tests/, mirroring python3-pydantic.
SRCREV = "bb49377d38afb3a9fac9022aaf2c8f59d4ef7cfe"
PV .= "+git"
SRC_URI = "git://github.com/hbldh/bleak;protocol=https;branch=develop \
	file://run-ptest \
	file://0001-bleak-Support-newer-uv_build-versions.patch \
"

inherit python_uv_build ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-asyncio \
	python3-pytest-asyncio \
	python3-pytest-cov \
	python3-bumble \
"

RDEPENDS:${PN} += " \
	python3-core (>3.8) \
	python3-dbus-fast \
	python3-xml \
"

do_install_ptest:append() {
	# tests/test_adapter_deprecation.py has plain "async def" tests with no
	# @pytest.mark.asyncio marker; they rely on upstream's pyproject.toml
	# setting asyncio_mode=auto, which isn't shipped into the ptest package.
	cat > ${D}${PTEST_PATH}/pytest.ini <<EOF
[pytest]
asyncio_mode = auto
EOF
}
