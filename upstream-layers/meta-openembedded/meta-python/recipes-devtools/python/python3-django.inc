SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "https://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

PYPI_PACKAGE = "django"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi ptest

SRC_URI += "file://run-ptest"

FILES:${PN} += "${datadir}/django"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "\
    python3-asgiref \
    python3-compression \
    python3-ctypes \
    python3-datetime \
    python3-email \
    python3-html \
    python3-json \
    python3-logging \
    python3-multiprocessing \
    python3-netserver \
    python3-numbers \
    python3-pkgutil \
    python3-pytz \
    python3-sqlparse \
    python3-threading \
    python3-unixadmin \
    python3-xml \
"

CVE_PRODUCT = "django"

do_install_ptest(){
    install -d ${D}${PTEST_PATH}//docs/_ext
    install -m 0644 ${S}/docs/_ext/github_links.py ${D}${PTEST_PATH}/docs/_ext

    cp -r ${S}/tests ${D}${PTEST_PATH}
    sed -i 's,/usr/bin/env python,/usr/bin/env python3,' ${D}${PTEST_PATH}/tests/runtests.py
    ln -sr ${D}${libdir}/python3.*/site-packages/django ${D}${PTEST_PATH}/django

    # make the top folder writable for all - django 6 creates a test db file in there
    # with a test user
    chmod 777 ${D}${PTEST_PATH}/tests
}

RDEPENDS:${PN}-ptest += " \
    gettext \
    python3-bcrypt \
    python3-compile \
    python3-docutils \
    python3-fcntl \
    python3-jinja2 \
    python3-misc \
    python3-numpy \
    python3-pillow \
    python3-pyyaml \
    python3-sqlite3 \
    python3-statistics \
    python3-tblib \
    python3-zoneinfo \
    tzdata \
"
