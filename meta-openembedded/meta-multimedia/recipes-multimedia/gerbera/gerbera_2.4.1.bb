Description = "Gerbera is a UPnP media server which allows you to stream your digital media through your home network and consume it on a variety of UPnP compatible devices."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=25cdec9afe3f1f26212ead6bd2f7fac8"

SRC_URI = "git://github.com/gerbera/gerbera.git;protocol=https;branch=master"
SRCREV = "870dd32d42f626dc2246d34b2224ffb6fd73e205"

S = "${WORKDIR}/git"

DEPENDS = "pugixml sqlite3 zlib fmt spdlog util-linux-libuuid libupnp libnsl2"

SYSTEMD_SERVICE:${PN} = "gerbera.service"

inherit cmake pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} curl taglib inotify exif matroska magic js"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=TRUE,-DWITH_SYSTEMD=FALSE,systemd"
PACKAGECONFIG[taglib] = "-DWITH_TAGLIB=TRUE,-DWITH_TAGLIB=FALSE,taglib"
PACKAGECONFIG[curl] = "-DWITH_CURL=TRUE,-DWITH_CURL=FALSE,curl"
PACKAGECONFIG[inotify] = "-DWITH_INOTIFY=TRUE,-DWITH_INOTIFY=FALSE,inotify-tools"
PACKAGECONFIG[avcodec] = "-DWITH_AVCODEC=TRUE,-DWITH_AVCODEC=FALSE,ffmpeg"
PACKAGECONFIG[wavpack] = "-DWITH_WAVPACK=TRUE,-DWITH_WAVPACK=FALSE,wavpack"
PACKAGECONFIG[exif] = "-DWITH_EXIF=TRUE,-DWITH_EXIF=FALSE,libexif"
PACKAGECONFIG[exiv2] = "-DWITH_EXIV2=TRUE,-DWITH_EXIV2=FALSE,exiv2"
PACKAGECONFIG[matroska] = "-DWITH_MATROSKA=TRUE,-DWITH_MATROSKA=FALSE,libebml libmatroska"
PACKAGECONFIG[magic] = "-DWITH_MAGIC=TRUE,-DWITH_MAGIC=FALSE,file"
PACKAGECONFIG[js] = "-DWITH_JS=TRUE,-DWITH_JS=FALSE,duktape"

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
