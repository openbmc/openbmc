require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI += "file://0001-fix-test_msgfmt_error_including_non_ascii-test.patch"
SRC_URI[sha256sum] = "461c5dd06d2ea16bd5ca37d3f46e4def1d6b0fe7588c6f4e2119517bb0af8b2d"
