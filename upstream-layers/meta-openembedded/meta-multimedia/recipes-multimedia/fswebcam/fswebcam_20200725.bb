SUMMARY = "Webcam image grabber and manipulation application"
DESCRIPTION = "\
    fswebcam captures images from a V4L1/V4L2 compatible device or file, \
    averages them to reduce noise and draws a caption using the GD Graphics \
    Library which also handles compressing the image to PNG or JPEG. The \
    resulting image is saved to a file or sent to stdio where it can be piped \
    to something like ncftpput or scp. \
"
HOMEPAGE = "http://www.sanslogic.co.uk/fswebcam/"
BUGTRACKER = "https://codeberg.org/fsphil/fswebcam/issues"
SECTION = "graphics"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "gd"

SRC_URI = "git://github.com/fsphil/fswebcam.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "db35d4bbd336885a44f017ff142bc9523dbdce3c"

inherit autotools-brokensep
