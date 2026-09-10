require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "9d4d93be539a18ab80d058eb515900e10951e04c537c5a6b394fc49528d3251f"
