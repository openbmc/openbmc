SUMMARY = "Python bindings for mpv"
DESCRIPTION = "python-mpv is a ctypes-based python interface to the mpv media player"
HOMEPAGE = "https://github.com/jaseg/python-mpv"
SECTION = "devel/python"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "95dbd4d869cd8de7a27914ad33e776accc0f01cb82a6d5b31ca439ade2208ffb"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    mpv \
    python3-ctypes \
"
# RDEPENDS on mpv, which propagates ffmpeg's LICENSE_FLAGS = "commercial";
# carry the same flag so this recipe is cleanly skipped (in both world and
# universe) unless "commercial" is in LICENSE_FLAGS_ACCEPTED, rather than
# surfacing as an unbuildable "Nothing RPROVIDES 'mpv'" dependency.
LICENSE_FLAGS = "commercial"
