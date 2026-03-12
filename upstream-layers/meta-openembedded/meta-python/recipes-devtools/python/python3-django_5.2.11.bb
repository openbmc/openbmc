require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "7f2d292ad8b9ee35e405d965fbbad293758b858c34bbf7f3df551aeeac6f02d3"
