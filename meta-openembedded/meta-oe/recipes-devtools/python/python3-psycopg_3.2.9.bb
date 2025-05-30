SUMMARY = "Psycopg 3 is the implementation of a PostgreSQL adapter for Python."
DESCRIPTION = "Psycopg is the most popular PostgreSQL adapter for the Python \
programming language. Its core is a complete implementation of the Python DB \
API 2.0 specifications. Several extensions allow access to many of the \
features offered by PostgreSQL."

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI[sha256sum] = "2fbb46fcd17bc81f993f28c47f1ebea38d66ae97cc2dbc3cad73b37cefbff700"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "libpq"
