SRCNAME = "email"
SRC_URI = "https://pypi.python.org/packages/source/e/${SRCNAME}/${SRCNAME}-${PV}.tar.gz;name=tarball file://LICENSE file://setup.patch"
SRC_URI[tarball.sha256sum] = "e2254c9b4e4cf33553f7dfc85b500eea4c3e96733736f38c9dbfc3dcc6303705"
SRC_URI[tarball.md5sum] = "67707dc0fab874edc5ac45f95ec9ec87"
require python-email-utils-standalone.inc
