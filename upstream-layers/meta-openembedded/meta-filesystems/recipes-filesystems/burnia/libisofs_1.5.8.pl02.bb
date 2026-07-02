SUMMARY = "library to create an ISO-9660 filesystem"
HOMEPAGE = "https://libburnia-project.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYRIGHT;md5=1c1e5a960562d65f6449bb1d21e1dffc"

SRC_URI = "http://files.libburnia-project.org/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "10bd584d8f00d8091e814902b9f0a3e209f16e938f510fc23ba05f3fa469db5a"

# The patchlevel (.pl02) tarball unpacks into a directory without the
# patchlevel suffix.
S = "${UNPACKDIR}/libisofs-${@d.getVar('PV').split('.pl')[0]}"

DEPENDS = "acl zlib"

inherit autotools

BBCLASSEXTEND = "native"
