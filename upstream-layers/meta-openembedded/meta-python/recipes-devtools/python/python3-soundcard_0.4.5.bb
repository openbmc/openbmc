SUMMARY = "Python library for accessing soundcards"
DESCRIPTION = "SoundCard is a library for playing and recording audio without resorting to a CPython extension"
HOMEPAGE = "https://github.com/bastibe/SoundCard"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e39df1540c06c17ca310ce87c36b042c"

SRC_URI[sha256sum] = "07272ba927e32cafdf634e4a1ca53b9a3218321a60c7d2e08f54b832a56946aa"

inherit pypi setuptools3 features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio"

RDEPENDS:${PN} += " \
    libpulse \
    pulseaudio-server \
    python3-cffi \
    python3-numpy \
"
