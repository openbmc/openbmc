HOMEPAGE = "https://github.com/boto/boto"
SUMMARY = "Amazon Web Services API"
DESCRIPTION = "\
  Boto3 is the Amazon Web Services (AWS) Software Development Kit (SDK) for Python, \
  which allows Python developers to write software that makes use of services like \
  Amazon S3 and Amazon EC2. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "e2d2824ba6459b330d097e94039a9c4f96ae3f4bcdc731d620589ad79dcd16d3"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-botocore python3-urllib3 python3-unixadmin"
