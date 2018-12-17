DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PYPI_PACKAGE := "Pint"

inherit pypi

SRC_URI[md5sum] = "e1f80f3f8fc4e61f68ad3912db26b3a8"
SRC_URI[sha256sum] = "afcf31443a478c32bbac4b00337ee9026a13d0e2ac83d30c79151462513bb0d4"

BBCLASSEXTEND = "native"
