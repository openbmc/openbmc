SUMMARY = "Python library for accessing soundcards"
DESCRIPTION = "SoundCard is a library for playing and recording audio without resorting to a CPython extension"
HOMEPAGE = "https://github.com/bastibe/SoundCard"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e39df1540c06c17ca310ce87c36b042c"

SRC_URI[sha256sum] = "9b46d64a26f97cd7d88bcfc385c97c069f97c5ec3404e4e7c23771598a8cf47b"

inherit pypi setuptools3 features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio"

RDEPENDS:${PN} += " \
    libpulse \
    pulseaudio-server \
    python3-cffi \
    python3-numpy \
"
