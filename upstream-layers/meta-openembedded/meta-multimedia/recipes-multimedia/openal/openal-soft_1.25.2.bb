SUMMARY = "OpenAL is a cross-platform 3D audio API"
HOMEPAGE = "http://kcat.strangesoft.net/openal.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0f159f19f9377e1895fbb477d5a7953e"

inherit cmake pkgconfig

DEPENDS = "zlib libsndfile1"

SRCREV = "b2c48f7718ef3fcf67921a8b6534c4914e328970"
SRC_URI = "git://github.com/kcat/openal-soft.git;protocol=https;branch=master;tag=${PV}"

# openal-soft 1.25.x annotates realtime-critical mixer functions with
# [[clang::nonblocking]] and, even with ALSOFT_WERROR=OFF, hardcodes
# -Werror=function-effects for CXX. clang-22's function-effects analysis
# then fails the build on calls into non-nonblocking std::variant::emplace.
# Pre-seed the feature-detection cache var so neither -Wfunction-effects
# nor its -Werror promotion is added.
EXTRA_OECMAKE:append:toolchain-clang = " -DHAVE_WFUNCTION_EFFECTS=OFF"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pipewire pulseaudio', d)}"
PACKAGECONFIG[alsa] = "-DALSOFT_BACKEND_ALSA=ON,-DALSOFT_BACKEND_ALSA=OFF,alsa-lib"
PACKAGECONFIG[oss] = "-DALSOFT_BACKEND_OSS=ON,-DALSOFT_BACKEND_OSS=OFF"
PACKAGECONFIG[pulseaudio] = "-DALSOFT_BACKEND_PULSEAUDIO=ON,-DALSOFT_BACKEND_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[pipewire] = "-DALSOFT_BACKEND_PIPEWIRE=ON,-DALSOFT_BACKEND_PIPEWIRE=OFF,pipewire"
PACKAGECONFIG[examples] = "-DALSOFT_EXAMPLES=ON,-DALSOFT_EXAMPLES=OFF"
PACKAGECONFIG[sdl2] = "-DALSOFT_BACKEND_SDL2=ON,-DALSOFT_BACKEND_SDL2=OFF,virtual/libsdl2 ffmpeg"

FILES:${PN} += "${datadir}"
